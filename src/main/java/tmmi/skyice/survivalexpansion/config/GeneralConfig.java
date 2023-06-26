package tmmi.skyice.survivalexpansion.config;

import com.fndream.tomlconfig.AutoLoadTomlConfig;
import com.fndream.tomlconfig.annotation.TableField;
import com.fndream.tomlconfig.annotation.TableName;
import org.tomlj.TomlTable;
@TableName("general")
public class GeneralConfig extends AutoLoadTomlConfig {

    @TableField(topComment = """
            Hard mode, if true, the player will be kicked out of the server after death 3 times.Default:true.
            硬核模式,如果为true,玩家死亡3次后将被踢出服务器。默认值：true""")
    private boolean hardMode = true;
    public GeneralConfig() {
        super(null);
    }
    public GeneralConfig(TomlTable source) {
        super(source);
        load();
    }

    public boolean isHardMode() {
        return hardMode;
    }
}
