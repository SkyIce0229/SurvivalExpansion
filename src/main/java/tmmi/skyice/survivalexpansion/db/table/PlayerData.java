package tmmi.skyice.survivalexpansion.db.table;

import tmmi.skyice.survivalexpansion.db.annotation.TableName;
import tmmi.skyice.survivalexpansion.util.LogUtil;
import tmmi.skyice.survivalexpansion.features.HardModeHandle;

import java.sql.Timestamp;
@TableName("survivalexpansion_player_data")
public class PlayerData extends ActiveRecordModel<PlayerData> {
    //把表的数据映射到这个entity里
    private Long id;
    private String username;
    private Integer respawnAvailable;
    private Timestamp lastRespawnSettlementTime;

    @Override
    public PlayerData init() {
        this.username = "";
        this.respawnAvailable = HardModeHandle.hardModeConfig.getRespawnAvailableLimit();
        this.lastRespawnSettlementTime = new Timestamp(System.currentTimeMillis());
        return this;
    }

    @Override
    public PlayerData copy() {
        return new PlayerData().setId(id);
    }

    @Override
    protected Long fetchId() {
        return id;
    }

    public Long getId() {
        return id;
    }
    //void改成SurvivalExpansionPlayerData能链式调用：data.setid().setname()
    public PlayerData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PlayerData setUsername(String username) {
        this.username = username;
        return this;
    }

    public Integer getRespawnAvailable() {
        updateRespawnAvailable();
        return respawnAvailable;
    }
    //因为要精确所以迫不得已写这，一般尽量别往实体类里写业务
    private void updateRespawnAvailable() {
        //更新可用复活次数逻辑
        int respawnLimit = HardModeHandle.hardModeConfig.getRespawnAvailableLimit();
        if (respawnAvailable == null || respawnAvailable >= respawnLimit) {
            return;
        }
        LogUtil.debug("最后重生结算时间戳：{}",lastRespawnSettlementTime);
        if (lastRespawnSettlementTime == null || lastRespawnSettlementTime.getTime() == -30610224000000L) {
            return;
        }

        //获取当前时间
        long currentTime = System.currentTimeMillis();
        //获取距离上次结算时间差
        long diffTime = currentTime - lastRespawnSettlementTime.getTime();
        //获取增加的复活次数
        int addRespawnAvailable = (int) (diffTime / HardModeHandle.hardModeConfig.getRespawnAvailableCoolDown());
        if (addRespawnAvailable > 0) {
            //增加的复活次数如果超过上限，就设置为上限
            respawnAvailable = Math.min(respawnAvailable + addRespawnAvailable, respawnLimit);
            //更新最后一次结算时间
            lastRespawnSettlementTime = new Timestamp(currentTime - diffTime % HardModeHandle.hardModeConfig.getRespawnAvailableCoolDown());
        }
    }
    public PlayerData setRespawnAvailable(Integer respawnAvailable) {
        this.respawnAvailable = Math.min(respawnAvailable, HardModeHandle.hardModeConfig.getRespawnAvailableLimit());
        return this;
    }
    public PlayerData addRespawnAvailable(int add) {
        this.respawnAvailable = Math.min(respawnAvailable + add ,HardModeHandle.hardModeConfig.getRespawnAvailableLimit());
        return this;
    }

    public Timestamp getLastRespawnSettlementTime() {
        return lastRespawnSettlementTime;
    }

    public PlayerData setLastRespawnSettlementTime(Timestamp lastRespawnSettlementTime) {
        this.lastRespawnSettlementTime = lastRespawnSettlementTime;
        return this;
    }
}
