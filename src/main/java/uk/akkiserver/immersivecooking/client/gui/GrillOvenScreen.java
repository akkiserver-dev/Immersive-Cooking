package uk.akkiserver.immersivecooking.client.gui;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.akkiserver.immersivecooking.common.gui.GrillOvenMenu;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import javax.annotation.Nonnull;
import java.util.List;

public class GrillOvenScreen extends ICContainerScreen<GrillOvenMenu> {
    private static final ResourceLocation TEXTURE = Resource.texture("gui/grill_oven.png");

    public GrillOvenScreen(GrillOvenMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
    }
}
