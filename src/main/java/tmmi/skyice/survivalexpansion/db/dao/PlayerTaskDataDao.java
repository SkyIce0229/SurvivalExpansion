package tmmi.skyice.survivalexpansion.db.dao;

import tmmi.skyice.survivalexpansion.db.table.PlayerTaskData;
import tmmi.skyice.survivalexpansion.db.util.DB;

public class PlayerTaskDataDao {
    private static final String SELECT_BY_NAME = "select * from `survivalexpansion_player_task_data` where username = ?";
    public static PlayerTaskData selectByName(String name) {
        return DB.executeQuery(PlayerTaskData.class, SELECT_BY_NAME, name);
    }
}
