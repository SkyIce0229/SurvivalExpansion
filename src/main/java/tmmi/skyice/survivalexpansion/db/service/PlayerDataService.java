package tmmi.skyice.survivalexpansion.db.service;

import tmmi.skyice.survivalexpansion.db.dao.PlayerDataDao;
import tmmi.skyice.survivalexpansion.db.table.PlayerData;

import java.util.Optional;

public class PlayerDataService {
    private PlayerDataService() {
    }
    public static PlayerData getByName(String name) {
        return PlayerDataDao.selectByName(name);
    }
    public static PlayerData getByNameOrDefault(String name) {
        return Optional.ofNullable(getByName(name)).orElse(new PlayerData().init());
    }
}
