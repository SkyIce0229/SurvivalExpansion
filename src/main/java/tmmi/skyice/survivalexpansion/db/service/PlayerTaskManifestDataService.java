package tmmi.skyice.survivalexpansion.db.service;

import tmmi.skyice.survivalexpansion.db.dao.PlayerTaskManifestDataDao;
import tmmi.skyice.survivalexpansion.db.table.PlayerTaskManifestData;

import java.time.LocalDate;
import java.util.List;

public class PlayerTaskManifestDataService {
    private PlayerTaskManifestDataService() {

    }
    public static List<PlayerTaskManifestData> listByUsername(String username) {
        return PlayerTaskManifestDataDao.selectListByUsername(username);
    }
    public static List<PlayerTaskManifestData> listByUsernameDate(String username, LocalDate date) {
        return PlayerTaskManifestDataDao.selectByUsernameDate(username,date);
    }
    public static int deleteByUsername(String username) {
        return PlayerTaskManifestDataDao.deleteByUsername(username);
    }
    public static int deleteByUsernameAndLine(String username,int line) {
        return PlayerTaskManifestDataDao.deleteByUsernameAndLine(username,line);
    }

}
