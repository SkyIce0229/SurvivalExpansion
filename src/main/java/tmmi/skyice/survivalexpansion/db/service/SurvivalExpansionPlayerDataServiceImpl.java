package tmmi.skyice.survivalexpansion.db.service;

import tmmi.skyice.survivalexpansion.db.dao.SurvivalExpansionPlayerDataDao;
import tmmi.skyice.survivalexpansion.db.entity.SurvivalExpansionPlayerData;

import java.util.concurrent.ConcurrentHashMap;

public class SurvivalExpansionPlayerDataServiceImpl implements SurvivalExpansionPlayerDataService {
    private SurvivalExpansionPlayerDataServiceImpl() {

    }
    public static SurvivalExpansionPlayerDataService getInstance() {
        return Holder.INSTANCE;
    }
    private static class Holder {
        private static final SurvivalExpansionPlayerDataService INSTANCE = ServiceFactory.createService(SurvivalExpansionPlayerDataServiceImpl.class);
    }
    public static ConcurrentHashMap<String, SurvivalExpansionPlayerData> cache = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> cacheNameIndex = new ConcurrentHashMap<>();
    @Override
    public int save(SurvivalExpansionPlayerData data) {
        return SurvivalExpansionPlayerDataDao.insert(data);
    }
    @Override
    public int update(SurvivalExpansionPlayerData data) {
        int result = SurvivalExpansionPlayerDataDao.update(data);
        if (cache.containsKey(String.valueOf(data.getId()))) {
            cache.remove(String.valueOf(data.getId()));
        }
        return result;
    }
    @Override
    public SurvivalExpansionPlayerData findByName(String name) {
        if (cacheNameIndex.containsKey(name) && cache.containsKey(cacheNameIndex.get(name))){
            return cache.get(cacheNameIndex.get(name));
        }
        SurvivalExpansionPlayerData data = SurvivalExpansionPlayerDataDao.selectByName(name);
        if (data == null) {
            return null;
        }
        if (cacheNameIndex.containsKey(name)) {
            cacheNameIndex.put(data.getName(),String.valueOf(data.getId()));
        }
        cache.put(String.valueOf(data.getId()),data);
        return data;
    }
}
