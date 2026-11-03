package uk.akkiserver.immersivecooking.client;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.CookpotScreen;
import uk.akkiserver.immersivecooking.client.gui.FoodFermenterScreen;
import uk.akkiserver.immersivecooking.client.gui.GrillOvenScreen;
import uk.akkiserver.immersivecooking.client.render.SlicerRenderer;
import uk.akkiserver.immersivecooking.client.model.SlicerModel;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.block.multiblock.CookpotMultiblock;
import uk.akkiserver.immersivecooking.common.block.multiblock.FoodFermenterMultiblock;
import uk.akkiserver.immersivecooking.common.block.multiblock.GrillOvenMultiblock;
import uk.akkiserver.immersivecooking.common.block.multiblock.ICTemplateMultiblock;
import uk.akkiserver.immersivecooking.common.crafting.RecipeReloadListener;
import uk.akkiserver.immersivecooking.common.fluid.ICFluids;
import uk.akkiserver.immersivecooking.common.util.Resource;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ImmersiveCooking.MODID, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.HIGH, ClientSetup::recipesUpdated);
    }

    private static void recipesUpdated(net.neoforged.neoforge.client.event.RecipesUpdatedEvent event) {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        var connection = minecraft.getConnection();
        if (!minecraft.hasSingleplayerServer() && connection != null)
            RecipeReloadListener.loadRecipes(event.getRecipeManager(), connection.registryAccess());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ICFluids.FLUID_TYPE_EXTENSIONS.forEach(event::registerFluidType);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(Resource.model("block/multiblock/cookpot"));
        event.register(Resource.model("block/multiblock/grill_oven"));
        event.register(Resource.model("block/multiblock/food_fermenter"));
    }

    @SubscribeEvent
    public static void modifyBakedModels(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((location, model) -> location.id().equals(Resource.mod("slicer"))
                && !location.variant().equals("inventory") ? new SlicerModel(model) : model);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ICContent.Multiblock.SLICER.masterBE().get(), context -> new SlicerRenderer());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ICContent.MenuTypes.GRILL_OVEN.getType(), GrillOvenScreen::new);
        event.register(ICContent.MenuTypes.COOKPOT.getType(), CookpotScreen::new);
        event.register(ICContent.MenuTypes.FOOD_FERMENTER.getType(), FoodFermenterScreen::new);

        ICFluids.ALL_ENTRIES.forEach(entry -> {
            ItemBlockRenderTypes.setRenderLayer(entry.getStill(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(entry.getFlowing(), RenderType.translucent());
        });

        //setupManual();
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
