package tmmi.skyice.survivalexpansion.config;

import com.fndream.tomlconfig.AutoLoadTomlConfig;
import com.fndream.tomlconfig.annotation.TableField;
import com.fndream.tomlconfig.annotation.TableName;
import org.tomlj.TomlTable;

@TableName("setting")
public class SettingConfig extends AutoLoadTomlConfig {
    @TableField(topComment = """
            Respawn available cool down time, unit: millisecond. Default:12 hours.
            复活可用冷却时间，单位：毫秒。""")
    private long RespawnAvailableCoolDown = 12 * 60 * 60 * 1000;
    @TableField(topComment = """
            Respawn available limit.Default:3.
            复活可用次数。 默认 3 次。""")
    private int RespawnAvailableLimit = 3;
    public SettingConfig() {
        super(null);
    }

    public SettingConfig(TomlTable source) {
        super(source);
        load();
    }

    public long getRespawnAvailableCoolDown() {
        return RespawnAvailableCoolDown;
    }

    public int getRespawnAvailableLimit() {
        return RespawnAvailableLimit;
    }

}
