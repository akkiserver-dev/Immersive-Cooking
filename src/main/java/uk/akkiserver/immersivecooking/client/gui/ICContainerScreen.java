package uk.akkiserver.immersivecooking.client.gui;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.common.gui.CokeOvenMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ICContainerScreen<M extends AbstractContainerMenu> extends IEContainerScreen<M> {
    public ICContainerScreen(M inventorySlotsIn, Inventory inv, Component title, ResourceLocation background) {
        super(inventorySlotsIn, inv, title, background);
    }
}
