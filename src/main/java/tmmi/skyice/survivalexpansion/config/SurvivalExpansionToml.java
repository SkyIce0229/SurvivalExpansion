package tmmi.skyice.survivalexpansion.config;


import com.fndream.tomlconfig.AutoLoadTomlConfig;
import org.tomlj.TomlTable;

public class SurvivalExpansionToml extends AutoLoadTomlConfig {
    private DatabaseConfig database = new DatabaseConfig();
    private GeneralConfig generalConfig = new GeneralConfig();
    private SettingConfig settingConfig = new SettingConfig();

    public SurvivalExpansionToml(){
        super(null);
    }
    public SurvivalExpansionToml(TomlTable source) {
        super(source);
        this.load();
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    public SettingConfig getSettingConfig() {
        return settingConfig;
    }

    public void setSettingConfig(SettingConfig settingConfig) {
        this.settingConfig = settingConfig;
    }
}
