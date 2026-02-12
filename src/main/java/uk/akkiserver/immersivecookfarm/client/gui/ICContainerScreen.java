package uk.akkiserver.immersivecookfarm.client.gui;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ICContainerScreen<M extends AbstractContainerMenu> extends IEContainerScreen<M> {
    public ICContainerScreen(M menu, Inventory playerInventory, Component title, ResourceLocation background) {
        super(menu, playerInventory, title, background);
    }
}
