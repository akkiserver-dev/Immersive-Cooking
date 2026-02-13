package uk.akkiserver.immersivecooking.client.gui;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.akkiserver.immersivecooking.common.gui.CookpotMenu;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import javax.annotation.Nonnull;
import java.util.List;

public class CookpotScreen extends ICContainerScreen<CookpotMenu> {
    private static final ResourceLocation TEXTURE = Resource.texture("gui/cookpot.png");

    public CookpotScreen(CookpotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
    }

    @Nonnull
    @Override
    protected List<InfoArea> makeInfoAreas() {
        return ImmutableList.of(new EnergyInfoArea(leftPos + 137, topPos + 22, menu.energyStorage));
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my) {
        int processTick = menu.data.get(0);
        int maxTick = menu.data.get(1);
        if (maxTick > 0) {
            int w = (int) (16 * (processTick / (float) maxTick));
            graphics.blit(TEXTURE, leftPos + 91, topPos + 19, 176, 0, w, 12);
        }
    }
}
