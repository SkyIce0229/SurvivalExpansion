package tmmi.skyice.survivalexpansion.config;


import com.fndream.tomlconfig.AutoLoadTomlConfig;
import org.tomlj.TomlTable;

public class SurvivalExpansionToml extends AutoLoadTomlConfig {
    private DatabaseConfig database = new DatabaseConfig();
    private HardModeConfig hardModeConfig = new HardModeConfig();
    private DailyTaskConfig dailyTaskConfig = new DailyTaskConfig();

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

    public SurvivalExpansionToml setDatabase(DatabaseConfig database) {
        this.database = database;
        return this;
    }

    public HardModeConfig getHardModeConfig() {
        return hardModeConfig;
    }

    public SurvivalExpansionToml setHardModeConfig(HardModeConfig hardModeConfig) {
        this.hardModeConfig = hardModeConfig;
        return this;
    }

    public DailyTaskConfig getDailyTaskConfig() {
        return dailyTaskConfig;
    }

    public SurvivalExpansionToml setDailyTaskConfig(DailyTaskConfig dailyTaskConfig) {
        this.dailyTaskConfig = dailyTaskConfig;
        return this;
    }
}
