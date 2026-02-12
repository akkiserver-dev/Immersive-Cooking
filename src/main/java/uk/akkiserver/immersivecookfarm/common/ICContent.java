package uk.akkiserver.immersivecookfarm.common;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;
import uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.CookpotMultiblock;
import uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.GrillOvenMultiblock;
import uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.logic.CookpotLogic;
import uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.logic.GrillOvenLogic;
import uk.akkiserver.immersivecookfarm.common.fluids.ICFluids;
import uk.akkiserver.immersivecookfarm.common.gui.CookpotMenu;
import uk.akkiserver.immersivecookfarm.common.gui.GrillOvenMenu;

import java.util.Collection;
import java.util.List;

import static uk.akkiserver.immersivecookfarm.ImmersiveCookFarm.MODID;

public final class ICContent {
    public static final Logger LOGGER = LoggerFactory.getLogger(ImmersiveCookFarm.MODID + "/Content");

    public static class Multiblock {
        public static final MultiblockRegistration<GrillOvenLogic.State> GRILL_OVEN = ICRegisters
                .registerStoneMultiblock(
                        "grill_oven",
                        new GrillOvenLogic(),
                        () -> GrillOvenMultiblock.INSTANCE,
                        builder -> builder.gui(MenuTypes.GRILL_OVEN));

        public static final MultiblockRegistration<CookpotLogic.State> COOKPOT = ICRegisters
                .registerMetalMultiblock(
                        "cookpot",
                        new CookpotLogic(),
                        () -> CookpotMultiblock.INSTANCE,
                        builder -> builder.gui(MenuTypes.COOKPOT).redstone(s -> s.rsState));

        public static final MultiblockRegistration<CookpotLogic.State> FOOD_FERMENTER = ICRegisters
                .registerMetalMultiblock(
                        "food_fermenter",
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

        public static final IEMenuTypes.MultiblockContainer<CookpotLogic.State, CookpotMenu> FOOD_FERMENTER = IEMenuTypes
                .registerMultiblock(
                        "food_fermenter",
                        CookpotMenu::makeServer,
                        CookpotMenu::makeClient);

        public static void forceClassLoad() {
        }
    }

    public static class Tabs {
        public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = ICRegisters.registerCreativeTab("main",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup." + MODID))
                        .icon(() -> new ItemStack(
                                Multiblock.COOKPOT.blockItem().get()))
                        .displayItems((params, output) -> {
                            Collection<RegistryObject<Item>> itemRegistries = ICRegisters.ITEM_REGISTER.getEntries();
                            for (RegistryObject<Item> itemRegistryObject : itemRegistries) {
                                if (!(itemRegistryObject.get() instanceof MultiblockItem)) {
                                    output.accept(itemRegistryObject.get());
                                }
                            }
                        })
                        .build());

        public static void forceClassLoad() {
        }
    }

    public static class Sounds {
        public static final RegistryObject<SoundEvent> COOKPOT_ACTIVE = ICRegisters.registerSoundEvent("block.cookpot.boil");

        public static void forceClassLoad() {
        }
    }

    public static class Fluids {
        public static final ICFluids.FluidEntry APPLE_JUICE = ICFluids.FluidEntry.make("apple_juice", 0xAFFFE74B);

        // Red Grapes
        public static final ICFluids.FluidEntry RED_GRAPE_JUICE = ICFluids.FluidEntry.make("red_grapejuice", 0xFF7D2ED8);
        public static final ICFluids.FluidEntry RED_TAIGA_GRAPE_JUICE = ICFluids.FluidEntry.make("red_taiga_grapejuice", 0xFF7D2ED8);
        public static final ICFluids.FluidEntry RED_JUNGLE_GRAPE_JUICE = ICFluids.FluidEntry.make("red_jungle_grapejuice", 0xFF7D2ED8);
        public static final ICFluids.FluidEntry RED_SAVANNA_GRAPE_JUICE = ICFluids.FluidEntry.make("red_savanna_grapejuice", 0xFF7D2ED8);

        // White Grapes
        public static final ICFluids.FluidEntry WHITE_GRAPE_JUICE = ICFluids.FluidEntry.make("white_grapejuice", 0xFF70812D);
        public static final ICFluids.FluidEntry WHITE_TAIGA_GRAPE_JUICE = ICFluids.FluidEntry.make("white_taiga_grapejuice", 0xFF70812D);
        public static final ICFluids.FluidEntry WHITE_JUNGLE_GRAPE_JUICE = ICFluids.FluidEntry.make("white_jungle_grapejuice", 0xFF70812D);
        public static final ICFluids.FluidEntry WHITE_SAVANNA_GRAPE_JUICE = ICFluids.FluidEntry.make("white_savanna_grapejuice", 0xFF70812D);

        public static void forceClassLoad() {
        }
    }

    public static void init() {
        Multiblock.forceClassLoad();
        MenuTypes.forceClassLoad();
        Tabs.forceClassLoad();
        Sounds.forceClassLoad();
        Fluids.forceClassLoad();
        MultiblockHandler.registerMultiblock(GrillOvenMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(CookpotMultiblock.INSTANCE);
    }
}
