package uk.akkiserver.immersivecooking;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecooking.client.gui.GrillOvenScreen;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.ICRegisters;

@Mod(ImmersiveCooking.MODID)
public class ImmersiveCooking {
    public static final String MODID = "immersivecooking";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ImmersiveCooking(FMLJavaModLoadingContext ctx) {
        ICRegisters.init(ctx.getModEventBus());
        ICContent.init();
    }
}
