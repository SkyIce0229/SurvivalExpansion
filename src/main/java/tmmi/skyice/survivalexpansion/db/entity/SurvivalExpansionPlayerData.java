package tmmi.skyice.survivalexpansion.db.entity;

import tmmi.skyice.survivalexpansion.SurvivalExpansionMod;
import tmmi.skyice.survivalexpansion.db.service.SurvivalExpansionPlayerDataServiceImpl;
import tmmi.skyice.survivalexpansion.util.LogUtil;

import java.sql.Timestamp;

public class SurvivalExpansionPlayerData {
    //把表的数据映射到这个entity里
    private Long id;
    private String name;
    private Integer respawnAvailable;
    private Timestamp lastRespawnSettlementTime;

    public SurvivalExpansionPlayerData copy() {
        return new SurvivalExpansionPlayerData().setId(id);
    }
    public int updateById () {
        return SurvivalExpansionPlayerDataServiceImpl.getInstance().update(this);
    }
    public Long getId() {
        return id;
    }
    //void改成SurvivalExpansionPlayerData能链式调用：data.setid().setname()
    public SurvivalExpansionPlayerData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SurvivalExpansionPlayerData setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getRespawnAvailable() {
        updateRespawnAvailable();
        return respawnAvailable;
    }
    private void updateRespawnAvailable() {
        //更新可用复活次数逻辑
        int respawnLimit = SurvivalExpansionMod.settingConfig.getRespawnAvailableLimit();
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
        int addRespawnAvailable = (int) (diffTime / SurvivalExpansionMod.settingConfig.getRespawnAvailableCoolDown());
        if (addRespawnAvailable > 0) {
            //增加的复活次数如果超过上限，就设置为上限
            respawnAvailable = Math.min(respawnAvailable + addRespawnAvailable, respawnLimit);
            //更新最后一次结算时间
            lastRespawnSettlementTime = new Timestamp(currentTime - diffTime % SurvivalExpansionMod.settingConfig.getRespawnAvailableCoolDown());
        }
    }
    public SurvivalExpansionPlayerData setRespawnAvailable(Integer respawnAvailable) {
        this.respawnAvailable = Math.min(respawnAvailable, SurvivalExpansionMod.settingConfig.getRespawnAvailableLimit());
        return this;
    }

    public Timestamp getLastRespawnSettlementTime() {
        return lastRespawnSettlementTime;
    }

    public SurvivalExpansionPlayerData setLastRespawnSettlementTime(Timestamp lastRespawnSettlementTime) {
        this.lastRespawnSettlementTime = lastRespawnSettlementTime;
        return this;
    }

}
