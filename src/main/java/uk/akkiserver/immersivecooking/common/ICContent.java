package uk.akkiserver.immersivecooking.common;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.GrillOvenMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic;
import uk.akkiserver.immersivecooking.common.gui.GrillOvenMenu;

public final class ICContent {
    public static final Logger LOGGER = LoggerFactory.getLogger(ImmersiveCooking.MODID + "/Content");

    public static class Multiblock {
        public static final MultiblockRegistration<GrillOvenLogic.State> GRILL_OVEN = ICRegisters.registerStoneMultiblock(
                "grill_oven",
                    new GrillOvenLogic(),
                    () -> GrillOvenMultiblock.INSTANCE,
                    builder -> builder.gui(MenuTypes.GRILL_OVEN)
                );

        public static void forceClassLoad() {}
    }

    public static class MenuTypes {
        public static final IEMenuTypes.MultiblockContainer<GrillOvenLogic.State, GrillOvenMenu> GRILL_OVEN = IEMenuTypes.registerMultiblock(
                "grill_oven",
                GrillOvenMenu::makeServer,
                GrillOvenMenu::makeClient
        );

        public static void forceClassLoad() {}
    }

    public static class Recipes {

    }

    public static void init() {
        Multiblock.forceClassLoad();
        MenuTypes.forceClassLoad();
        MultiblockHandler.registerMultiblock(GrillOvenMultiblock.INSTANCE);
    }
}
