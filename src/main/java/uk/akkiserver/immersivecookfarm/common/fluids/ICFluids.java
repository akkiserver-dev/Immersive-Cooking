package uk.akkiserver.immersivecookfarm.common.fluids;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecookfarm.common.ICRegisters;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ICFluids {
    public static final List<FluidEntry> ALL_ENTRIES = new ArrayList<>();
    public static final Set<RegistryObject<? extends LiquidBlock>> ALL_FLUID_BLOCKS = new HashSet<>();

    public record FluidEntry(
            RegistryObject<ICFluid> flowing,
            RegistryObject<ICFluid> still,
            RegistryObject<ICFluidBlock> block,
            RegistryObject<BucketItem> bucket,
            RegistryObject<FluidType> type,
            List<Property<?>> properties
    ) {
        private static final ResourceLocation WATER_STILL = Resource.mc("block/water_still");
        private static final ResourceLocation WATER_FLOW = Resource.mc("block/water_flow");

        public static FluidEntry make(String name) {
            return make(name, 0, WATER_STILL, WATER_FLOW, null, ImmutableList.of(), 0xFFFFFFFF);
        }

        public static FluidEntry make(String name, int tintColor) {
            return make(name, 0, WATER_STILL, WATER_FLOW, null, ImmutableList.of(), tintColor);
        }

        public static FluidEntry make(String name, ResourceLocation stillTex, ResourceLocation flowingTex) {
            return make(name, 0, stillTex, flowingTex, null, ImmutableList.of(), 0xFFFFFFFF);
        }

        public static FluidEntry make(
                String name, ResourceLocation stillTex, ResourceLocation flowingTex,
                Consumer<FluidType.Properties> buildAttributes
        ) {
            return make(name, 0, stillTex, flowingTex, buildAttributes, ImmutableList.of(), 0xFFFFFFFF);
        }

        public static FluidEntry make(
                String name, int burnTime,
                ResourceLocation stillTex, ResourceLocation flowingTex
        ) {
            return make(name, burnTime, stillTex, flowingTex, null, ImmutableList.of(), 0xFFFFFFFF);
        }

        public static FluidEntry make(
                String name, int burnTime,
                ResourceLocation stillTex, ResourceLocation flowingTex,
                @Nullable Consumer<FluidType.Properties> buildAttributes,
                List<Property<?>> properties,
                int tintColor
        ) {
            return make(
                    name, burnTime, stillTex, flowingTex,
                    ICFluid::new, ICFluid.Flowing::new,
                    buildAttributes, properties,
                    tintColor
            );
        }

        public static FluidEntry make(
                String name, int burnTime,
                ResourceLocation stillTex, ResourceLocation flowingTex,
                Function<FluidEntry, ? extends ICFluid> makeStill,
                Function<FluidEntry, ? extends ICFluid> makeFlowing,
                @Nullable Consumer<FluidType.Properties> buildAttributes,
                List<Property<?>> properties,
                int tintColor
        ) {
            FluidType.Properties builder = FluidType.Properties.create()
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
            if (buildAttributes != null)
                buildAttributes.accept(builder);

            RegistryObject<FluidType> type = ICRegisters.FLUID_TYPE_REGISTER.register(
                    name, () -> makeTypeWithTextures(builder, stillTex, flowingTex, tintColor)
            );

            Mutable<FluidEntry> thisMutable = new MutableObject<>();
            RegistryObject<ICFluid> still = ICRegisters.FLUID_REGISTER.register(
                    name, () -> ICFluid.makeFluid(makeStill, thisMutable.getValue())
            );
            RegistryObject<ICFluid> flowing = ICRegisters.FLUID_REGISTER.register(
                    name + "_flowing", () -> ICFluid.makeFluid(makeFlowing, thisMutable.getValue())
            );

            RegistryObject<ICFluidBlock> block = ICRegisters.BLOCK_REGISTER.register(
                    name + "_fluid_block",
                    () -> new ICFluidBlock(thisMutable.getValue(), Properties.copy(Blocks.WATER))
            );

            RegistryObject<BucketItem> bucket = ICRegisters.registerItem(
                    name + "_bucket", () -> makeBucket(still, burnTime)
            );

            FluidEntry entry = new FluidEntry(flowing, still, block, bucket, type, properties);
            thisMutable.setValue(entry);
            ALL_FLUID_BLOCKS.add(block);
            ALL_ENTRIES.add(entry);
            return entry;
        }

        private static FluidType makeTypeWithTextures(
                FluidType.Properties builder,
                ResourceLocation stillTex,
                ResourceLocation flowingTex,
                int tintColor
        ) {
            return new FluidType(builder) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return stillTex;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return flowingTex;
                        }

                        @Override
                        public int getTintColor() {
                            return tintColor;
                        }
                    });
                }
            };
        }

        private static BucketItem makeBucket(RegistryObject<ICFluid> still, int burnTime) {
            return new BucketItem(
                    still, new Item.Properties()
                    .stacksTo(1)
                    .craftRemainder(Items.BUCKET)
            ) {
                @Override
                public @NotNull ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
                    return new FluidBucketWrapper(stack);
                }

                @Override
                public int getBurnTime(ItemStack itemStack, RecipeType<?> type) {
                    return burnTime;
                }
            };
        }

        public ICFluid getFlowing() {
            return flowing.get();
        }

        public ICFluid getStill() {
            return still.get();
        }

        public ICFluidBlock getBlock() {
            return block.get();
        }

        public BucketItem getBucket() {
            return bucket.get();
        }

        public RegistryObject<ICFluid> getStillGetter() {
            return still;
        }
    }
}