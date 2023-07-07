package tmmi.skyice.survivalexpansion.db.dao;

import tmmi.skyice.survivalexpansion.db.table.PlayerData;
import tmmi.skyice.survivalexpansion.db.util.DB;



public class PlayerDataDao {
    private static final String SELECT_BY_NAME = "select * from `survivalexpansion_player_data` where username = ?";
    public static PlayerData selectByName(String name) {
        return DB.executeQuery(PlayerData.class, SELECT_BY_NAME, name);
    }
}
