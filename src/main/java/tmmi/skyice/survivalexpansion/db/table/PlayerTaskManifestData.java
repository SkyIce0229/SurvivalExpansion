package tmmi.skyice.survivalexpansion.db.table;

import net.minecraft.item.Item;
import tmmi.skyice.survivalexpansion.db.ItemTypeHandler;
import tmmi.skyice.survivalexpansion.db.annotation.TableField;
import tmmi.skyice.survivalexpansion.db.annotation.TableName;

import java.time.LocalDate;

@TableName("survivalexpansion_player_task_manifest_data")
public class PlayerTaskManifestData extends ActiveRecordModel {
    private Long id;
    private String username;
    @TableField(typeHandle = ItemTypeHandler.class)
    private Item requireItem;
    private int requireCount;
    private LocalDate date;

    @Override
    public Object copy() {
        return new PlayerTaskManifestData().setId(id);
    }

    @Override
    protected Long fetchId() {
        return id;
    }

    public Long getId() {
        return id;
    }

    public PlayerTaskManifestData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PlayerTaskManifestData setUsername(String username) {
        this.username = username;
        return this;
    }

    public Item getRequireItem() {
        return requireItem;
    }

    public PlayerTaskManifestData setRequireItem(Item requireItem) {
        this.requireItem = requireItem;
        return this;
    }

    public int getRequireCount() {
        return requireCount;
    }

    public PlayerTaskManifestData setRequireCount(int requireCount) {
        this.requireCount = requireCount;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public PlayerTaskManifestData setDate(LocalDate date) {
        this.date = date;
        return this;
    }
}
