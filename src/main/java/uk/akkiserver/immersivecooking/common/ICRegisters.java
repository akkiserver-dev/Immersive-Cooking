package uk.akkiserver.immersivecooking.common;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.ComparatorManager;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.multiblocks.component.MultiblockGui;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.blocks.ICFurnaceLikeMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.ICMultiblockBase;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static uk.akkiserver.immersivecooking.ImmersiveCooking.LOGGER;
import static uk.akkiserver.immersivecooking.ImmersiveCooking.MODID;

public final class ICRegisters {
    private static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();

    public static <B> DeferredRegister<B> createRegister(@NotNull IForgeRegistry<B> reg) {
        DeferredRegister<B> register = DeferredRegister.create(reg, MODID);
        REGISTERS.add(register);
        return register;
    }

    public static <B> DeferredRegister<B> createRegister(@NotNull ResourceKey<Registry<B>> reg) {
        DeferredRegister<B> register = DeferredRegister.create(reg, MODID);
        REGISTERS.add(register);
        return register;
    }

    public static final DeferredRegister<Block> BLOCK_REGISTER = createRegister(ForgeRegistries.BLOCKS);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTER = createRegister(ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final DeferredRegister<Item> ITEM_REGISTER = createRegister(ForgeRegistries.ITEMS);
    public static final DeferredRegister<Fluid> FLUID_REGISTER = createRegister(ForgeRegistries.FLUIDS);
    public static final DeferredRegister<FluidType> FLUID_TYPE_REGISTER = createRegister(ForgeRegistries.Keys.FLUID_TYPES);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = createRegister(ForgeRegistries.RECIPE_TYPES);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = createRegister(ForgeRegistries.RECIPE_SERIALIZERS);
    private static final DeferredRegister<SoundEvent> SOUND_EVENT = createRegister(ForgeRegistries.SOUND_EVENTS);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = createRegister(Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<MenuType<?>> MENU_REGISTER = createRegister(ForgeRegistries.MENU_TYPES);

    public static void init(IEventBus eventBus) {
        for (var reg : REGISTERS) {
            reg.register(eventBus);
            LOGGER.info("Registered {}", reg.getRegistryName());
        }
    }

    //
    // register methods from 'Immersive Petroleum' addon, thanks to Flaxbeaed,
    // TwistedGate and maintainers...
    //

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name,
            IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure) {
        return registerMetalMultiblock(name, logic, structure, null);
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name,
            IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure,
            @Nullable Consumer<IEMultiblockBuilder<S>> extras) {
        IEMultiblockBuilder<S> builder = new IEMultiblockBuilder<>(logic, name)
                .defaultBEs(BLOCK_ENTITY_TYPE_REGISTER)
                .defaultBlock(BLOCK_REGISTER, ITEM_REGISTER, IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get())
                .structure(structure);

        if (extras != null) {
            extras.accept(builder);
        }

        return builder.build();
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerStoneMultiblock(String name,
            IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure,
            @Nullable Consumer<MultiblockBuilder<S>> extras) {
        BlockBehaviour.Properties prop = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2, 20);

        MultiblockBuilder<S> builder = new MultiblockBuilder<>(logic, name)
                .structure(structure)
                .defaultBEs(BLOCK_ENTITY_TYPE_REGISTER)
                .customBlock(BLOCK_REGISTER, ITEM_REGISTER, mb -> new ICFurnaceLikeMultiblock<>(prop, mb),
                        MultiblockItem::new);

        if (extras != null) {
            extras.accept(builder);
        }

        return builder.build();
    }

    public static <S extends IMultiblockState> MultiblockRegistration<S> registerMultiblock(String name,
            IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure,
            @Nullable Consumer<MultiblockBuilder<S>> extras, BlockBehaviour.Properties prop) {
        MultiblockBuilder<S> builder = new MultiblockBuilder<>(logic, name)
                .structure(structure)
                .defaultBEs(BLOCK_ENTITY_TYPE_REGISTER)
                .customBlock(BLOCK_REGISTER, ITEM_REGISTER, mb -> new ICMultiblockBase<>(prop, mb),
                        MultiblockItem::new);

        if (extras != null) {
            extras.accept(builder);
        }

        return builder.build();
    }

    protected static class MultiblockBuilder<S extends IMultiblockState>
            extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>> {
        public MultiblockBuilder(IMultiblockLogic<S> logic, String name) {
            super(logic, Resource.mod(name));
        }

        public MultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState,
                BlockPos... positions) {
            redstoneAware();
            return selfWrappingComponent(new RedstoneControl<>(getState, positions));
        }

        public MultiblockBuilder<S> comparator(ComparatorManager<S> comparator) {
            withComparator();
            return super.selfWrappingComponent(comparator);
        }

        public MultiblockBuilder<S> gui(IEMenuTypes.MultiblockContainer<S, ?> menu) {
            return component(new MultiblockGui<>(menu));
        }

        @Override
        protected MultiblockBuilder<S> self() {
            return this;
        }
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor) {
        return registerBlock(name, blockConstructor, null);
    }

    public static <T extends Block> RegistryObject<T> registerMultiblockBlock(String name,
            Supplier<T> blockConstructor) {
        return registerBlock(name, blockConstructor, block -> new BlockItem(block, new Item.Properties()));
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor,
            @Nullable Function<T, ? extends BlockItem> blockItem) {
        RegistryObject<T> block = BLOCK_REGISTER.register(name, blockConstructor);
        if (blockItem != null) {
            registerItem(name, () -> blockItem.apply(block.get()));
        }
        return block;
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemConstructor) {
        return ITEM_REGISTER.register(name, itemConstructor);
    }

    public static <T extends Fluid> RegistryObject<T> registerFluid(String name, Supplier<T> fluidConstructor) {
        return FLUID_REGISTER.register(name, fluidConstructor);
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(String name,
            BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> valid) {
        return BLOCK_ENTITY_TYPE_REGISTER.register(name,
                () -> new BlockEntityType<>(factory, ImmutableSet.of(valid.get()), null));
    }

    public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockBlockEntity(
            String name, MultiblockBEType.BEWithTypeConstructor<T> factory, Supplier<? extends Block> valid) {
        return new MultiblockBEType<>(name, BLOCK_ENTITY_TYPE_REGISTER, factory, valid,
                state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE)
                        && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
    }

    public static <T extends RecipeSerializer<?>> RegistryObject<T> registerSerializer(String name,
            Supplier<T> serializer) {
        return RECIPE_SERIALIZERS.register(name, serializer);
    }

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name,
            Supplier<MenuType<T>> factory) {
        return MENU_REGISTER.register(name, factory);
    }

    public static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(Resource.mod(name)));
    }

    public static RegistryObject<CreativeModeTab> registerCreativeTab(String name, Supplier<CreativeModeTab> tab) {
        return CREATIVE_TABS.register(name, tab);
    }
}
