package tmmi.skyice.survivalexpansion.config;

import com.fndream.tomlconfig.AutoLoadTomlConfig;
import com.fndream.tomlconfig.annotation.TableField;
import org.tomlj.TomlTable;

import java.util.Arrays;
import java.util.List;

public class DailyTaskConfig extends AutoLoadTomlConfig {
    @TableField(topComment = {
            "The number of tasks to be completed each day.Default：5",
            "每日需要完成任务的数量。默认值：5"
    })
    private int taskCount = 5;
    @TableField(topComment = {
            "The item blacklist of the daily task, after filling in the item id, the item will not appear when the daily task is refreshed. Default: \"BEDROCK\",\"ELYTRA\".",
            "每日任务物品黑名单，往里面填入物品id后，生成的每日任务需求将不会出现以下物品。默认值：\"BEDROCK\",\"ELYTRA\"。"
    })
    private List<String> itemBlacklist = Arrays.asList(
            "BEDROCK",
            "ELYTRA",
            "*_SPAWN_EGG",
            "MUSIC_DISC_*"
    );


    public DailyTaskConfig() {
        super(null);
    }

    public DailyTaskConfig(TomlTable source) {
        super(source);
        this.load();
    }

    public int getTaskCount() {
        return taskCount;
    }

    public DailyTaskConfig setTaskCount(int taskCount) {
        this.taskCount = taskCount;
        return this;
    }

    public List<String> getItemBlacklist() {
        return itemBlacklist;
    }

    public DailyTaskConfig setItemBlacklist(List<String> itemBlacklist) {
        this.itemBlacklist = itemBlacklist;
        return this;
    }
}
