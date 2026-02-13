package uk.akkiserver.immersivecooking.client.gui;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.akkiserver.immersivecooking.common.gui.CookpotMenu;
import uk.akkiserver.immersivecooking.common.gui.FoodFermenterMenu;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import javax.annotation.Nonnull;
import java.util.List;

public class FoodFermenterScreen extends ICContainerScreen<FoodFermenterMenu> {
    private static final ResourceLocation TEXTURE = Resource.texture("gui/food_fermenter.png");

    public FoodFermenterScreen(FoodFermenterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
    }

    @Nonnull
    @Override
    protected List<InfoArea> makeInfoAreas() {
        return ImmutableList.of(
                new FluidInfoArea(menu.tank, new Rect2i(leftPos + 14, topPos + 20, 16, 47),
                        177, 31, 20, 51, TEXTURE),
                new EnergyInfoArea(leftPos + 157, topPos + 21, menu.energyStorage)
        );
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my) {

    }
}
