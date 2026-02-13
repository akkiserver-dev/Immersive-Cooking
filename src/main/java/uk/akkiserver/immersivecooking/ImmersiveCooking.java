package uk.akkiserver.immersivecooking;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.ICRegisters;

@Mod(ImmersiveCooking.MODID)
public class ImmersiveCooking {
    public static final String MODID = "immersivecooking";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ImmersiveCooking(FMLJavaModLoadingContext ctx) {
        ICRecipes.init();
        ICRegisters.init(ctx.getModEventBus());
        ICContent.init();
    }
}
