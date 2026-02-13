package uk.akkiserver.immersivecooking.client;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.CookpotScreen;
import uk.akkiserver.immersivecooking.client.gui.FoodFermenterScreen;
import uk.akkiserver.immersivecooking.client.gui.GrillOvenScreen;
import uk.akkiserver.immersivecooking.client.utils.ICBasicClientProperties;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.CookpotMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.GrillOvenMultiblock;
import uk.akkiserver.immersivecooking.common.utils.Resource;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ImmersiveCooking.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerAdditionalModels(net.minecraftforge.client.event.ModelEvent.RegisterAdditional event) {
        event.register(Resource.mod("block/multiblock/cookpot.obj"));
        event.register(Resource.mod("block/multiblock/grill_oven.obj"));
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ICContent.MenuTypes.GRILL_OVEN.getType(), GrillOvenScreen::new);
            MenuScreens.register(ICContent.MenuTypes.COOKPOT.getType(), CookpotScreen::new);
            MenuScreens.register(ICContent.MenuTypes.FOOD_FERMENTER.getType(), FoodFermenterScreen::new);
            ICBasicClientProperties.initModels();
        });

        setupManual();
    }

    public static void setupManual() {
        ManualInstance manual = ManualHelper.getManual();
        ResourceLocation main = Resource.mod("main");
        ResourceLocation multiblocks = Resource.mod("multiblocks");

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.readFromFile(Resource.mod("cookpot"));
        builder.addSpecialElement(new ManualEntry.SpecialElementData("cookpot0", 0,
                () -> new ManualElementMultiblock(manual, CookpotMultiblock.INSTANCE)));
        ManualEntry entry = builder.create();
        manual.addEntry(manual.getRoot().getOrCreateSubnode(main).getOrCreateSubnode(multiblocks), entry);

        builder = new ManualEntry.ManualEntryBuilder(manual);
        builder.readFromFile(Resource.mod("grill_oven"));
        builder.addSpecialElement(new ManualEntry.SpecialElementData("grill_oven0", 0,
                () -> new ManualElementMultiblock(manual, GrillOvenMultiblock.INSTANCE)));
        entry = builder.create();
        manual.addEntry(manual.getRoot().getOrCreateSubnode(main).getOrCreateSubnode(multiblocks), entry);
    }
}
