package uk.akkiserver.immersivecookfarm.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;
import uk.akkiserver.immersivecookfarm.data.blockstates.ICBlockStates;
import uk.akkiserver.immersivecookfarm.data.tags.ICBlockTags;
import uk.akkiserver.immersivecookfarm.data.tags.ICItemTags;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ImmersiveCookFarm.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ICDataGenerator {
    public static final Logger LOGGER = LogManager.getLogger(ImmersiveCookFarm.MODID + "/DataGenerator");

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
            var blockTags = new ICBlockTags(output, lookupProvider, existing);
            generator.addProvider(event.includeServer(), blockTags);
            generator.addProvider(event.includeServer(), new ICItemTags(output, lookupProvider, blockTags.contentsGetter(), existing));
        }
    }
}
