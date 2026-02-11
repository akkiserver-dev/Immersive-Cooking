package uk.akkiserver.immersivecooking.data;

import blusunrize.immersiveengineering.data.ItemModels;
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

@Mod.EventBusSubscriber(modid = ImmersiveCooking.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ICDataGenerator {
    public static final Logger LOGGER = LogManager.getLogger(ImmersiveCooking.MODID + "/DataGenerator");

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existing = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();

        if (event.includeServer()) {
            generator.addProvider(true, new ICMBTextureSourceApplier(generator.getPackOutput(), existing));
            ICBlockStates blockStates = new ICBlockStates(output, existing);
            generator.addProvider(true, blockStates);
            generator.addProvider(true, new ICItemModels(output, existing, blockStates));
        }
    }
}
