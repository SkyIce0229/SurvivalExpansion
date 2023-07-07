package tmmi.skyice.survivalexpansion.db.table;


import tmmi.skyice.survivalexpansion.db.annotation.TableName;
import java.time.LocalDate;

@TableName("survivalexpansion_player_task_data")
public class PlayerTaskData extends ActiveRecordModel<PlayerTaskData> {
    private Long id;
    private String username;
    private String taskData;
    private LocalDate refreshDate;
    private Integer successCount;
    //创建一个包含id值的新对象，用于更新数据库

    //动态生成update语句
    @Override
    public PlayerTaskData init() {
        this.username = "";
        this.taskData = "{}";
        this.refreshDate = LocalDate.of(1000,1,1);
        this.successCount = 0;
        return this;
    }

    @Override
    public PlayerTaskData copy() {
        return new PlayerTaskData().setId(id);
    }

    @Override
    protected Long fetchId() {
        return id;
    }

    public Long getId() {
        return id;
    }

    public PlayerTaskData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PlayerTaskData setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getTaskData() {
        return taskData;
    }

    public PlayerTaskData setTaskData(String taskData) {
        this.taskData = taskData;
        return this;
    }

    public LocalDate getRefreshDate() {
        return refreshDate;
    }

    public PlayerTaskData setRefreshDate(LocalDate refreshDate) {
        this.refreshDate = refreshDate;
        return this;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public PlayerTaskData setSuccessCount(Integer successCount) {
        this.successCount = successCount;
        return this;
    }
}
