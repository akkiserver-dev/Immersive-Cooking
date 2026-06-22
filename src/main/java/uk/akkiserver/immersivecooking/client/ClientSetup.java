package uk.akkiserver.immersivecooking.client;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.CookpotScreen;
import uk.akkiserver.immersivecooking.client.gui.FoodFermenterScreen;
import uk.akkiserver.immersivecooking.client.gui.FoodProcessorScreen;
import uk.akkiserver.immersivecooking.client.gui.GrillOvenScreen;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.CookpotMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.FoodFermenterMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.GrillOvenMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.ICTemplateMultiblock;
import uk.akkiserver.immersivecooking.common.fluids.ICFluids;
import uk.akkiserver.immersivecooking.common.utils.Resource;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ImmersiveCooking.MODID, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(Resource.mod("block/multiblock/cookpot.obj")));
        event.register(ModelResourceLocation.standalone(Resource.mod("block/multiblock/grill_oven.obj")));
        event.register(ModelResourceLocation.standalone(Resource.mod("block/multiblock/food_fermenter.obj")));
        event.register(ModelResourceLocation.standalone(Resource.mod("block/multiblock/food_processor.obj")));
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ICContent.MenuTypes.GRILL_OVEN.getType(), GrillOvenScreen::new);
        event.register(ICContent.MenuTypes.COOKPOT.getType(), CookpotScreen::new);
        event.register(ICContent.MenuTypes.FOOD_FERMENTER.getType(), FoodFermenterScreen::new);
        event.register(ICContent.MenuTypes.FOOD_PROCESSOR.getType(), FoodProcessorScreen::new);

        ICFluids.ALL_ENTRIES.forEach(entry -> {
            ItemBlockRenderTypes.setRenderLayer(entry.getStill(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(entry.getFlowing(), RenderType.translucent());
        });

        setupManual();
    }

    public static void setupManual() {
        ManualInstance manual = ManualHelper.getManual();
        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(manual);



        registerMultiblockManualPage(manual, builder, "cookpot", CookpotMultiblock.INSTANCE);
        registerMultiblockManualPage(manual, builder, "food_fermenter", FoodFermenterMultiblock.INSTANCE);
        registerMultiblockManualPage(manual, builder, "grill_oven", GrillOvenMultiblock.INSTANCE);
    }

    public static void registerMultiblockManualPage(ManualInstance manual, ManualEntry.ManualEntryBuilder builder, String id, ICTemplateMultiblock multiblock) {
        builder.readFromFile(Resource.mod(id));
        builder.addSpecialElement(new ManualEntry.SpecialElementData(id + 0, 0,
                () -> new ManualElementMultiblock(manual, multiblock)));
        ManualEntry entry = builder.create();
        manual.addEntry(manual.getRoot().getOrCreateSubnode(Resource.mod("main")).getOrCreateSubnode(Resource.mod("multiblocks")), entry);

    }
}
