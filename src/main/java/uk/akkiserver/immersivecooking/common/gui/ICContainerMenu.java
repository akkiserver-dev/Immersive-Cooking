package uk.akkiserver.immersivecooking.common.gui;

import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class ICContainerMenu extends IEContainerMenu {
    protected ICContainerMenu(MenuContext ctx) {
        super(ctx);
    }

    protected void addInventorySlots(Inventory playerInventory, int startY) {
        for (int row = 0; row < 3; row++) {
            for(int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, startY + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, startY + 58));
        }
    }

    protected void addInventorySlots(Inventory playerInventory) {
        addInventorySlots(playerInventory, 84);
    }
}
