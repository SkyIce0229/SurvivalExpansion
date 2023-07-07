package tmmi.skyice.survivalexpansion.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class InteractionScreenHandler extends ScreenHandler {
    public InteractionScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId);

        int rows = 3;
        int columns = 9;
        int startX = 8;
        int startY = 18;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int index = col + row * columns;
                int x = startX + col * 18;
                int y = startY + row * 18;
                this.addSlot(new Slot(inventory, index, x, y) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return false;
                    }
                });
            }
        }


        int playerInventoryStartX = 8;
        int playerInventoryStartY = 84;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                int x = playerInventoryStartX + col * 18;
                int y = playerInventoryStartY + row * 18;
                this.addSlot(new Slot(playerInventory, index, x, y) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return false;
                    }
                });
            }
        }


        int hotbarStartX = 8;
        int hotbarStartY = 142;

        for (int col = 0; col < 9; col++) {
            int x = hotbarStartX + col * 18;
            this.addSlot(new Slot(playerInventory, col, x, hotbarStartY) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    return false;
                }
            });
        }
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
