package tmmi.skyice.survivalexpansion.db.service;

import tmmi.skyice.survivalexpansion.db.entity.SurvivalExpansionPlayerData;

public interface SurvivalExpansionPlayerDataService {
    int save(SurvivalExpansionPlayerData data);
    int update(SurvivalExpansionPlayerData data);
    SurvivalExpansionPlayerData findByName(String name);
}
