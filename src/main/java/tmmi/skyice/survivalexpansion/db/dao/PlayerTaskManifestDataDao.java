package tmmi.skyice.survivalexpansion.db.dao;

import tmmi.skyice.survivalexpansion.db.table.PlayerTaskManifestData;
import tmmi.skyice.survivalexpansion.db.util.DB;

import java.time.LocalDate;
import java.util.List;

public class PlayerTaskManifestDataDao {
    private static final String SELECT_BY_USERNAME = "select * from `survivalexpansion_player_task_manifest_data` where username = ? order by id asc";
    private static final String SELECT_BY_USERNAME_DATE = "select * from `survivalexpansion_player_task_manifest_data` where username = ? AND date = ? order by id asc";
    private static final String DELETE_BY_USERNAME = "delete from `survivalexpansion_player_task_manifest_data` where username = ?";
    private static final String DELETE_BY_USERNAME_AND_ITEM = "delete from `survivalexpansion_player_task_manifest_data` where username = ?";

    public static List<PlayerTaskManifestData> selectListByUsername(String username) {
        return DB.executeQueryList(PlayerTaskManifestData.class,SELECT_BY_USERNAME,username);
    }
    public static List<PlayerTaskManifestData> selectByUsernameDate(String username, LocalDate date) {
        return DB.executeQueryList(PlayerTaskManifestData.class,SELECT_BY_USERNAME_DATE,username,date);
    }
    public static int deleteByUsername(String username) {
        return DB.executeUpdate(DELETE_BY_USERNAME,username);
    }
    public static int deleteByUsernameAndLine(String username,int line) {
        return DB.executeUpdate(DELETE_BY_USERNAME_AND_ITEM,username,line);
    }
}
