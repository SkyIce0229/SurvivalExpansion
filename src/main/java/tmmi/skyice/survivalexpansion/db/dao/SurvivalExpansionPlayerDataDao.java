package tmmi.skyice.survivalexpansion.db.dao;

import tmmi.skyice.survivalexpansion.db.entity.SurvivalExpansionPlayerData;
import tmmi.skyice.survivalexpansion.db.util.DBUtil;
import tmmi.skyice.survivalexpansion.db.util.SqlUtil;


public class SurvivalExpansionPlayerDataDao {
    public static int insert(SurvivalExpansionPlayerData data) {
        //插入命令
        String sql = "insert into `survivalexpansion_player_data` (name, respawn_available, last_respawn_settlement_time) values (?,?,?)";
        return DBUtil.executeUpdate(sql, data.getName(), data.getRespawnAvailable(), data.getLastRespawnSettlementTime());
    }
    public static int update(SurvivalExpansionPlayerData data) {
        //插入命令
        String sqlSet = SqlUtil.toSqlSet(data);
        String sql = "update `survivalexpansion_player_data` set " + sqlSet + " where id = ?";
        return DBUtil.executeUpdate(sql, data.getId());
    }
    public static SurvivalExpansionPlayerData selectByName(String name) {
        //插入命令
        String sql = "select * from `survivalexpansion_player_data` where name = ?";
        return DBUtil.executeQuery(sql, SurvivalExpansionPlayerData.class, name);
    }
}
