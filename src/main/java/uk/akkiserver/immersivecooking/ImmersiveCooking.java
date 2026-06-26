package uk.akkiserver.immersivecooking;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.ICRegisters;
import uk.akkiserver.immersivecooking.common.compat.Compatibility;

@Mod(ImmersiveCooking.MODID)
public class ImmersiveCooking {
    public static final String MODID = "immersivecooking";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ImmersiveCooking(IEventBus bus, ModContainer container) {
        ICRecipes.init();
        ICRegisters.init(bus);
        ICContent.init();
        ICRegisters.runCallbacks(bus);
        Compatibility.init();
    }
}
