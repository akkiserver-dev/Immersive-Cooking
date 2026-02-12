package uk.akkiserver.immersivecooking.common;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.CookpotMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.GrillOvenMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.CookpotLogic;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic;
import uk.akkiserver.immersivecooking.common.gui.CookpotMenu;
import uk.akkiserver.immersivecooking.common.gui.GrillOvenMenu;

import static uk.akkiserver.immersivecooking.ImmersiveCooking.MODID;

public final class ICContent {
    public static final Logger LOGGER = LoggerFactory.getLogger(ImmersiveCooking.MODID + "/Content");

    public static class Multiblock {
        public static final MultiblockRegistration<GrillOvenLogic.State> GRILL_OVEN = ICRegisters
                .registerStoneMultiblock(
                        "grill_oven",
                        new GrillOvenLogic(),
                        () -> GrillOvenMultiblock.INSTANCE,
                        builder -> builder.gui(MenuTypes.GRILL_OVEN));

        public static final MultiblockRegistration<CookpotLogic.State> COOKPOT = ICRegisters.registerMetalMultiblock(
                "cookpot",
                new CookpotLogic(),
                () -> CookpotMultiblock.INSTANCE,
                builder -> builder.gui(MenuTypes.COOKPOT).redstone(s -> s.rsState));

        public static void forceClassLoad() {
        }
    }

    public static class MenuTypes {
        public static final IEMenuTypes.MultiblockContainer<GrillOvenLogic.State, GrillOvenMenu> GRILL_OVEN = IEMenuTypes
                .registerMultiblock(
                        "grill_oven",
                        GrillOvenMenu::makeServer,
                        GrillOvenMenu::makeClient);

        public static final IEMenuTypes.MultiblockContainer<CookpotLogic.State, CookpotMenu> COOKPOT = IEMenuTypes
                .registerMultiblock(
                        "cookpot",
                        CookpotMenu::makeServer,
                        CookpotMenu::makeClient);

        public static void forceClassLoad() {
        }
    }

    public static class Tabs {
        public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = ICRegisters.registerCreativeTab("main",
                () -> CreativeModeTab.builder()
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup." + MODID))
                        .icon(() -> new net.minecraft.world.item.ItemStack(
                                ICContent.Multiblock.COOKPOT.blockItem().get()))
                        .displayItems((params, output) -> {

                        })
                        .build());

        public static void forceClassLoad() {
        }
    }

    public static class Sounds {
        public static final RegistryObject<net.minecraft.sounds.SoundEvent> COOKPOT_ACTIVE = ICRegisters.registerSoundEvent("block.cookpot.boil");

        public static void forceClassLoad() {
        }
    }

    public static void init() {
        Multiblock.forceClassLoad();
        MenuTypes.forceClassLoad();
        Tabs.forceClassLoad();
        Sounds.forceClassLoad();
        MultiblockHandler.registerMultiblock(GrillOvenMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(CookpotMultiblock.INSTANCE);
    }
}
