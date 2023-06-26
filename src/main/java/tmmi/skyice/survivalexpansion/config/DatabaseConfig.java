package tmmi.skyice.survivalexpansion.config;

import com.fndream.tomlconfig.AutoLoadTomlConfig;
import com.fndream.tomlconfig.annotation.TableField;
import com.fndream.tomlconfig.annotation.TableName;
import org.tomlj.TomlTable;

@TableName("database")
public class DatabaseConfig extends AutoLoadTomlConfig {
    @TableField(topComment = """
            Mysql Server ip.
            数据库服务器ip地址。""")
    private String ip = "localhost";
    @TableField(topComment = """
            Mysql Server port
            数据库服务器端口。""")
    private int port = 3306;
    @TableField(topComment = """
            Mysql Server username.
            数据库服务器用户名。""")
    private String username = "root";
    @TableField(topComment = """
            Mysql Server password.
            数据库服务器密码。""")
    private String password = "123456";
    @TableField(topComment = """
            Mysql Server database name,If don't understand, please don't change it.
            数据库服务器数据库名称,如果你不知道这是什么,请不要修改它。""")
    private String databaseName = "minecraft";
    public DatabaseConfig() {
        super(null);
    }

    public DatabaseConfig(TomlTable source) {
        super(source);
        load();
    }
    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
