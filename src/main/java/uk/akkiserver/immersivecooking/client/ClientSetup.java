package uk.akkiserver.immersivecooking.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.GrillOvenScreen;
import uk.akkiserver.immersivecooking.common.ICContent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ImmersiveCooking.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ICContent.MenuTypes.GRILL_OVEN.getType(), GrillOvenScreen::new));
    }
}
