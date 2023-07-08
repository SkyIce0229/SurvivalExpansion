package tmmi.skyice.survivalexpansion.screen.factory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import tmmi.skyice.survivalexpansion.SurvivalExpansionMod;
import tmmi.skyice.survivalexpansion.db.service.PlayerDataService;
import tmmi.skyice.survivalexpansion.db.service.PlayerTaskManifestDataService;
import tmmi.skyice.survivalexpansion.db.table.ActiveRecordModel;
import tmmi.skyice.survivalexpansion.db.table.PlayerData;
import tmmi.skyice.survivalexpansion.db.table.PlayerTaskManifestData;
import tmmi.skyice.survivalexpansion.db.util.DB;
import tmmi.skyice.survivalexpansion.screen.InteractionScreenHandler;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

public class DailyTasksScreen implements NamedScreenHandlerFactory {
    private static final ItemStack DECORATE_GLASS_PANE = new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
    private static final ItemStack SUCCESS_GLASS_PANE;
    // 玩家当前的任务数据
    private List<PlayerTaskManifestData> playerTasks;


    // 是否是新数据，如果为true，close时进行insert，否则进行update
    private boolean isNewTask;

    static {
        SUCCESS_GLASS_PANE = new ItemStack(Items.LIME_STAINED_GLASS_PANE, 1);
        SUCCESS_GLASS_PANE.setCustomName(Text.literal(Text.translatable("text.survival_expansion.item.finish_glass_pane").getString()));
    }

    @Override
    public Text getDisplayName() {
        return Text.literal(Text.translatable("text.survival_expansion.screen.daily_task.title").getString());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        Inventory inventory = createViewInventory(player);
        return new InteractionScreenHandler(syncId, playerInventory, inventory) {
            @Override
            public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                onSlotClickHandler(slotIndex, button, actionType, player, inventory);
            }

            @Override
            public void onClosed(PlayerEntity player) {
                super.onClosed(player);
                // insertOrUpdate 一般是不会用到了因为id和name冲突你更新不需要更新name插入又需要name那么逻辑肯定要if,既然都if了，就不需要insertOrUpdate

                //是否完成任务
                boolean isCompleted = true;
                if (!playerTasks.isEmpty()) {
                    for (PlayerTaskManifestData playerTask : playerTasks) {
                        //判断任务是否完成，如果完成，跳出循环，如果没完成，继续循环
                        if (playerTask.getRequireCount() > 0) {
                            isCompleted = false;
                            break;
                        }
                    }
                    //判断是否完成任务，如果没完成任务，执行保存数据操作。如果完成了，奖励玩家1次复活
                    if (isCompleted) {
                        player.sendMessage(Text.literal("任务完成!"));
                        //任务完成，奖励玩家1次复活
                        PlayerData playerData = PlayerDataService.getByNameOrDefault(player.getName().getString());
                        //获取玩家复活上限
                        int respawnAvailableLimit = SurvivalExpansionMod.survivalExpansionToml.getHardModeConfig().getRespawnAvailableLimit();
                        //获取玩家当前复活次数
                        int respawnAvailable = playerData.getRespawnAvailable();
                        //判断玩家复活次数是否达到上限，如果达到上限，不奖励，否则奖励
                        playerData.copy().setRespawnAvailable(Math.min(respawnAvailableLimit, respawnAvailable + 1)).updateById();
                    } else {
                        if (isNewTask) {
                            DB.executeTransaction(() -> playerTasks.forEach(ActiveRecordModel::insert));
                        } else {
                            DB.executeTransaction(() -> playerTasks.forEach(ActiveRecordModel::updateById));
                        }
                    }
                }
            }
        };
    }

    private Inventory createViewInventory(PlayerEntity player) {
        int taskCount = SurvivalExpansionMod.survivalExpansionToml.getDailyTaskConfig().getTaskCount();
        SimpleInventory inventory = new SimpleInventory(9 * 3);

        DB.executeTransaction(() -> {
            this.playerTasks = Optional.ofNullable(PlayerTaskManifestDataService.listByUsernameDate(player.getName().getString(), LocalDate.now()))
                    .map(list -> list.stream().limit(taskCount).toList()).orElse(null);

            if (playerTasks == null) {
                PlayerTaskManifestDataService.deleteByUsername(player.getName().getString());
                playerTasks = refreshManifest(player.getName().getString(), taskCount);
                isNewTask = true;
            } else {
                isNewTask = false;
            }


            int middleIndex = inventory.size() / 2;
            int middleStartIndex = middleIndex - taskCount / 2;

            IntStream.range(0, middleStartIndex).forEach(i -> inventory.setStack(i, DECORATE_GLASS_PANE));

            IntStream.range(0, taskCount).forEach(i -> {
                if (i < playerTasks.size()) {
                    if (playerTasks.get(i).getRequireCount() > 0) {
                        inventory.setStack(i + middleStartIndex, new ItemStack(playerTasks.get(i).getRequireItem(), playerTasks.get(i).getRequireCount()));
                    } else {
                        inventory.setStack(i + middleStartIndex, SUCCESS_GLASS_PANE);
                    }
                } else {
                    inventory.setStack(i + middleStartIndex, SUCCESS_GLASS_PANE);
                }

            });

            IntStream.range(middleStartIndex + taskCount, 27).forEach(i -> inventory.setStack(i, DECORATE_GLASS_PANE));

        });

        return inventory;
    }


    private List<PlayerTaskManifestData> refreshManifest(String username, int count) {
        List<PlayerTaskManifestData> result = new ArrayList<>();
        List<String> itemList = new ArrayList<>();
        List<Identifier> registerList = Registries.ITEM.getIds().stream().toList();
        List<String> blacklist = SurvivalExpansionMod.survivalExpansionToml.getDailyTaskConfig().getItemBlacklist();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            Identifier id = registerList.get(random.nextInt(registerList.size()));

            while (itemList.contains(id.toString())) {
                id = registerList.get(random.nextInt(registerList.size()));
            }

            String itemId = id.toString();
            boolean isBlacklisted = blacklist.stream().anyMatch(pattern -> itemId.endsWith(pattern.replace("*", "")));
            if (isBlacklisted) {
                continue;
            }

            Item item = Registries.ITEM.get(id);
            itemList.add(id.toString());
            result.add(new PlayerTaskManifestData().setUsername(username).setRequireItem(item).setRequireCount(random.nextInt(64) + 1).setDate(LocalDate.now()));
        }
        return result;
    }

    private void onSlotClickHandler(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, Inventory inventory) {
        //拦截非左右键的非点击事件
        if ((button != 0 && button != 1) || actionType != SlotActionType.PICKUP) {
            return;
        }
        int taskCount = SurvivalExpansionMod.survivalExpansionToml.getDailyTaskConfig().getTaskCount();
        int middleIndex = inventory.size() / 2;
        int middleStartIndex = middleIndex - taskCount / 2;
        //拦截非限定的任务格子以外的点击事件
        if (slotIndex < middleStartIndex || slotIndex > middleStartIndex + taskCount) {
            return;
        }
        //获取点击物品
        ItemStack clickItemStack = inventory.getStack(slotIndex);
        int clickItemStackCount = clickItemStack.getCount();

        if (ItemStack.areItemsEqual(clickItemStack, SUCCESS_GLASS_PANE) || ItemStack.areItemsEqual(clickItemStack, new ItemStack(Items.AIR))) {
            return;
        }

        PlayerInventory playerInventory = player.getInventory();
        boolean itemShortage = true; // 默认物品不足
        for (int i = 0; i < playerInventory.size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (ItemStack.areItemsEqual(itemStack, clickItemStack)) {
                int itemCount = itemStack.getCount();
                if (button == 0) {
                    player.getInventory().removeStack(i, 1);
                    inventory.removeStack(slotIndex, 1);
                    playerTasks.get(slotIndex - middleStartIndex).setRequireCount(clickItemStackCount - 1);
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.6f, 0.8f);
                    //slotIndex - middleStartIndex 是用来计算任务格子在任务列表中的索引位置的偏移量。在你的代码中，middleStartIndex 是任务列表的起始索引，slotIndex 是实际点击的物品格子的索引。
                    //
                    //通过将 slotIndex 减去 middleStartIndex，可以获取相对于任务列表的索引位置，从而正确地访问 playerTasks 列表中的对应任务对象。
                    //
                    //例如，假设 slotIndex 是 5，而 middleStartIndex 是 3，则 slotIndex - middleStartIndex 的结果为 2，表示物品格子在任务列表中的索引位置为 2。
                    //
                    //这样，你就可以使用 playerTasks.get(slotIndex - middleStartIndex) 来访问正确的任务对象。

                } else {
                    int needCount = Math.min(itemCount, clickItemStackCount);
                    player.getInventory().removeStack(i, needCount);
                    inventory.removeStack(slotIndex, needCount);
                    playerTasks.get(slotIndex - middleStartIndex).setRequireCount(clickItemStackCount - needCount);
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.6f, 1.0f);
                }
                if (playerTasks.get(slotIndex - middleStartIndex).getRequireCount() == 0) {
                    inventory.setStack(slotIndex, SUCCESS_GLASS_PANE);
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.6f, 0.8f);
                }
                itemShortage = false;
                break;
            }

        }
        if (itemShortage) {
            // 物品不足提示逻辑，例如发送消息给玩家
            player.sendMessage(Text.literal("物品不足"));
        }
    }
}
