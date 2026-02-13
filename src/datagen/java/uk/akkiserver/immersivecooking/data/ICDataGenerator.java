package uk.akkiserver.immersivecooking.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.data.blockstates.ICBlockStates;
import uk.akkiserver.immersivecooking.data.tags.ICBlockTags;
import uk.akkiserver.immersivecooking.data.tags.ICFluidTags;
import uk.akkiserver.immersivecooking.data.tags.ICItemTags;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ImmersiveCooking.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ICDataGenerator {
    public static final Logger LOGGER = LogManager.getLogger(ImmersiveCooking.MODID + "/DataGenerator");

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existing = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new ICMBTextureSourceApplier(generator.getPackOutput(), existing));
            ICBlockStates blockStates = new ICBlockStates(output, existing);
            generator.addProvider(true, blockStates);
            generator.addProvider(true, new ICItemModels(output, existing, blockStates));
            generator.addProvider(true, new ICRecipeProvider(output));
            generator.addProvider(event.includeServer(), new ICFluidTags(output, lookupProvider, existing));
            var blockTags = new ICBlockTags(output, lookupProvider, existing);
            generator.addProvider(event.includeServer(), blockTags);
            generator.addProvider(event.includeServer(), new ICItemTags(output, lookupProvider, blockTags.contentsGetter(), existing));
        }
    }
}
