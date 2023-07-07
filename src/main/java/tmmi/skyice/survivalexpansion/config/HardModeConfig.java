package tmmi.skyice.survivalexpansion.config;

import com.fndream.tomlconfig.AutoLoadTomlConfig;
import com.fndream.tomlconfig.annotation.TableField;
import org.tomlj.TomlTable;

public class HardModeConfig extends AutoLoadTomlConfig {

    @TableField(topComment = {
            "Hard mode, if true, the player will be kicked out of the server after death 3 times.Default:true.",
            "硬核模式,如果为true,玩家死亡3次后将被踢出服务器。默认值：true"
    })
    private boolean enable = true;
    @TableField(topComment = {
            "Respawn available cool down time, unit: millisecond. Default:12 hours.",
            "复活可用冷却时间，单位：毫秒。"})
    private long RespawnAvailableCoolDown = 12 * 60 * 60 * 1000;
    @TableField(topComment = {
            "Respawn available limit.Default:3.",
            "复活可用次数。 默认 3 次。"
    })
    private int RespawnAvailableLimit = 3;

    public HardModeConfig() {
        super(null);
    }

    public HardModeConfig(TomlTable source) {
        super(source);
        this.load();
    }

    public boolean isEnable() {
        return enable;
    }

    public HardModeConfig setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public long getRespawnAvailableCoolDown() {
        return RespawnAvailableCoolDown;
    }

    public HardModeConfig setRespawnAvailableCoolDown(long respawnAvailableCoolDown) {
        RespawnAvailableCoolDown = respawnAvailableCoolDown;
        return this;
    }

    public int getRespawnAvailableLimit() {
        return RespawnAvailableLimit;
    }

    public HardModeConfig setRespawnAvailableLimit(int respawnAvailableLimit) {
        RespawnAvailableLimit = respawnAvailableLimit;
        return this;
    }
}
