package tmmi.skyice.survivalexpansion.init;

import com.fndream.tomlconfig.util.TomlUtil;
import org.tomlj.Toml;
import org.tomlj.TomlTable;
import tmmi.skyice.survivalexpansion.SurvivalExpansionMod;
import tmmi.skyice.survivalexpansion.config.SurvivalExpansionToml;
import tmmi.skyice.survivalexpansion.db.util.DBUtil;
import tmmi.skyice.survivalexpansion.util.LogUtil;
import tmmi.skyice.survivalexpansion.util.TomlConfigJarDownload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Init {
    public static void init() {
        LogUtil.info("-------------------------------------------------");
        LogUtil.info("|                   开始初始化资源                 |");
        LogUtil.info("-------------------------------------------------");
        TomlConfigJarDownload.Download();
        loadConfig();
        initDatabase();
    }
    private static void initConfig() throws IOException {
        TomlUtil.writeConfigFile("config/SurvivalExpansion/survival-expansion.toml",new SurvivalExpansionToml(null));
    }
    private static void loadConfig() {
        if (!new File("config/SurvivalExpansion/survival-expansion.toml").exists()){
            try {
                initConfig();
            } catch (IOException e) {
                throw new RuntimeException("初始化配置文件失败",e);
            }
        }

        TomlTable toml;
        try {
            toml = Toml.parse(Paths.get("config/SurvivalExpansion/survival-expansion.toml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SurvivalExpansionMod.survivalExpansionConfig = new SurvivalExpansionToml(toml);
        SurvivalExpansionMod.databaseConfig = SurvivalExpansionMod.survivalExpansionConfig.getDatabase();
        SurvivalExpansionMod.generalConfig = SurvivalExpansionMod.survivalExpansionConfig.getGeneralConfig();
        SurvivalExpansionMod.settingConfig = SurvivalExpansionMod.survivalExpansionConfig.getSettingConfig();
    }

    private static void initDatabase() {

        //数据库地址
        String url = "jdbc:mysql://" + SurvivalExpansionMod.databaseConfig.getIp() + ":" + SurvivalExpansionMod.databaseConfig.getPort() + "/";
        //数据库名字
        String databaseName = SurvivalExpansionMod.databaseConfig.getDatabaseName();
        String username = SurvivalExpansionMod.databaseConfig.getUsername();
        String password = SurvivalExpansionMod.databaseConfig.getPassword();

        //连接mysql服务
        try (Connection serverConnection = DriverManager.getConnection(url,username,password);
             Statement stmt = serverConnection.createStatement()) {
            //建库语句
            String createSql = "create database if not exists " + databaseName;
            //尝试创建数据库
            try {
                if (stmt.executeUpdate(createSql) == 1){
                    LogUtil.debug("数据库创建成功");
                }
            } catch (SQLException e) {
                throw new RuntimeException("数据库创建失败",e);
            }
        }catch (SQLException e) {
            throw new RuntimeException("mysql服务连接失败",e);
        }
        //连接数据库
        Connection dbConnection = null;
        Statement dbstmt = null;
        //尝试建表
        String createtable = "CREATE TABLE IF NOT EXISTS `survivalexpansion_player_data` (`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',`name` varchar(36) NOT NULL,`respawn_available` int NOT NULL DEFAULT '3',`last_respawn_settlement_time` datetime NOT NULL DEFAULT '1000-01-01 00:00:00', PRIMARY KEY (`id`), UNIQUE KEY `uk_name` (`name`) USING BTREE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3";

        try {
            dbConnection = DBUtil.getConnection();
            dbstmt = dbConnection.createStatement();
            try {
                int i = dbstmt.executeUpdate(createtable);
                System.out.println(i);
                if (i == 1){
                    LogUtil.LOGGER.info("数据表创建成功");
                }
            } catch (SQLException e) {
                throw new RuntimeException("数据表创建失败",e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败",e);
        }finally {
            DBUtil.close(null,dbstmt,dbConnection);
        }
    }

}
