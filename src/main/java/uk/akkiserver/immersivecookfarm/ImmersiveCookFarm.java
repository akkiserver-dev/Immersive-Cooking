package uk.akkiserver.immersivecookfarm;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.ICRecipes;
import uk.akkiserver.immersivecookfarm.common.ICRegisters;

@Mod(ImmersiveCookFarm.MODID)
public class ImmersiveCookFarm {
    public static final String MODID = "immersivecookfarm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ImmersiveCookFarm(FMLJavaModLoadingContext ctx) {
        ICRecipes.init();
        ICRegisters.init(ctx.getModEventBus());
        ICContent.init();
    }
}
