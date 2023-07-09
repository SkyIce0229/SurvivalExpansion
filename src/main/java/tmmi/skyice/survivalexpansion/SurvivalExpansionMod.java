package tmmi.skyice.survivalexpansion;

import com.fndream.tomlconfig.util.TomlUtil;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.tomlj.Toml;
import org.tomlj.TomlTable;
import tmmi.skyice.survivalexpansion.config.SurvivalExpansionToml;
import tmmi.skyice.survivalexpansion.db.util.DB;
import tmmi.skyice.survivalexpansion.features.HardModeHandle;
import tmmi.skyice.survivalexpansion.util.LogUtil;
import tmmi.skyice.survivalexpansion.util.TomlConfigJarDownload;
import tmmi.skyice.survivalexpansion.command.CommandRegisterHandler;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class SurvivalExpansionMod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "survivalexpansion";
    public static String version;
    public static SurvivalExpansionToml survivalExpansionToml;
    public static SurvivalExpansionMod instance;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitializeServer() {
        instance = this;
        LogUtil.info("-------------------------------------------------");
        LogUtil.info("|                   开始初始化资源                 |");
        LogUtil.info("-------------------------------------------------");
        TomlConfigJarDownload.Download();
        loadConfig();
        initDatabase();
        CommandRegistrationCallback.EVENT.register(new CommandRegisterHandler());
        HardModeHandle.initialize();
    }

    private static void initConfig() throws IOException {
        TomlUtil.writeConfigFile("config/SurvivalExpansion/survival-expansion.toml", new SurvivalExpansionToml());
    }

    private static void loadConfig() {
        if (!new File("config/SurvivalExpansion/survival-expansion.toml").exists()) {
            try {
                initConfig();
            } catch (IOException e) {
                throw new RuntimeException("初始化配置文件失败", e);
            }
        }

        TomlTable toml;
        try {
            toml = Toml.parse(Paths.get("config/SurvivalExpansion/survival-expansion.toml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SurvivalExpansionMod.survivalExpansionToml = new SurvivalExpansionToml(toml);
    }

    private static void initDatabase() {

        //数据库地址
        String url = "jdbc:mysql://" + survivalExpansionToml.getDatabase().getIp() + ":" + survivalExpansionToml.getDatabase().getPort() + "/";
        //数据库名字
        String databaseName = survivalExpansionToml.getDatabase().getDatabaseName();
        String username = survivalExpansionToml.getDatabase().getUsername();
        String password = survivalExpansionToml.getDatabase().getPassword();

        //连接mysql服务
        try (Connection serverConnection = DriverManager.getConnection(url, username, password);
             Statement stmt = serverConnection.createStatement()) {
            //建库语句
            String createSql = "create database if not exists " + databaseName;
            //尝试创建数据库
            try {
                if (stmt.executeUpdate(createSql) == 1) {
                    LogUtil.debug("数据库创建成功");
                }
            } catch (SQLException e) {
                throw new RuntimeException("数据库创建失败", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("mysql服务连接失败", e);
        }
        //连接数据库
        Connection dbConnection = null;
        Statement dbstmt = null;
        //尝试建表
        String[] createTable = {"CREATE TABLE IF NOT EXISTS `survivalexpansion_player_data` (`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',`username` varchar(36) NOT NULL,`respawn_available` int NOT NULL DEFAULT '3',`last_respawn_settlement_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`), UNIQUE KEY `uk_name` (`username`) USING BTREE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3",
                "CREATE TABLE IF NOT EXISTS `survivalexpansion_player_task_data` (`id` bigint NOT NULL AUTO_INCREMENT,`username` varchar(36) NOT NULL,`success_count` int NOT NULL DEFAULT '0',PRIMARY KEY (`id`),UNIQUE KEY `uk_name` (`username`) USING BTREE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3",
                "CREATE TABLE IF NOT EXISTS `survivalexpansion_player_task_manifest_data` (`id` bigint NOT NULL AUTO_INCREMENT,`username` varchar(36) NOT NULL,`require_item` varchar(128) NOT NULL DEFAULT 'AIR',`require_count` int NOT NULL DEFAULT '0',`date` date NOT NULL DEFAULT (curdate()),PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;"};

        try {
            dbConnection = DB.getConnection();
            dbstmt = dbConnection.createStatement();
            try {
                for (int i = 0; i < 3; i++) {
                    int j = dbstmt.executeUpdate(createTable[i]);
                    System.out.println(i);
                    if (j == 1) {
                        LogUtil.LOGGER.info("数据表{}创建成功", i);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("数据表创建失败", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败", e);
        } finally {
            DB.close(null, dbstmt, dbConnection);
        }
    }


}
