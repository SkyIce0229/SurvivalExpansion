package tmmi.skyice.survivalexpansion.db.service;

import tmmi.skyice.survivalexpansion.db.dao.PlayerTaskDataDao;
import tmmi.skyice.survivalexpansion.db.table.PlayerTaskData;

import java.util.Optional;

public class PlayerTaskDataService {
    private PlayerTaskDataService() {

    }
    public static PlayerTaskData getByName(String name) {
        return PlayerTaskDataDao.selectByName(name);
    }
    public static PlayerTaskData getByNameOrDefault(String name) {
        return Optional.ofNullable(getByName(name)).orElse(new PlayerTaskData().init());
    }
}
