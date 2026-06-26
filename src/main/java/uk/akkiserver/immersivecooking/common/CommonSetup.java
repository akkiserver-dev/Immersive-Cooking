package uk.akkiserver.immersivecooking.common;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.crafting.RecipeReloadListener;
import uk.akkiserver.immersivecooking.common.fluid.ICFluids;

@EventBusSubscriber(modid = ImmersiveCooking.MODID)
public final class CommonSetup {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ICFluids.ALL_ENTRIES.forEach(fluidEntry -> {
            event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), fluidEntry.getBucket());
        });
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new RecipeReloadListener(event.getServerResources()));
    }
}
