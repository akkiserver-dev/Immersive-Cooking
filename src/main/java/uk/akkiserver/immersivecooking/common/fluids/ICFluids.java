package uk.akkiserver.immersivecooking.common.fluids;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import uk.akkiserver.immersivecooking.common.ICRegisters;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ICFluids {
    public static final List<FluidEntry> ALL_ENTRIES = new ArrayList<>();
    public static final Set<DeferredHolder<Block, ? extends LiquidBlock>> ALL_FLUID_BLOCKS = new HashSet<>();
    public static final Map<IClientFluidTypeExtensions, FluidType> FLUID_TYPE_EXTENSIONS = new HashMap<>();

    public record FluidEntry(
            DeferredHolder<Fluid, ICFluid> flowing,
            DeferredHolder<Fluid, ICFluid> still,
            DeferredHolder<Block, ICFluidBlock> block,
            DeferredHolder<Item, BucketItem> bucket,
            DeferredHolder<FluidType, FluidType> type,
            List<Property<?>> properties
    ) {
        private static final ResourceLocation WATER_STILL = Resource.mc("block/water_still");
        private static final ResourceLocation WATER_FLOW = Resource.mc("block/water_flow");

        public static FluidEntry make(String name) {
            return make(name, 0, WATER_STILL, WATER_FLOW, null, ImmutableList.of(), 0xFFFFFFFF, null, null, -1, -1);
        }

        public static FluidEntry make(String name, int tintColor) {
            return make(name, 0, WATER_STILL, WATER_FLOW, null, ImmutableList.of(), tintColor, null, null, -1, -1);
        }

        public static FluidEntry make(String name, ResourceLocation stillTex, ResourceLocation flowingTex) {
            return make(name, 0, stillTex, flowingTex, null, ImmutableList.of(), 0xFFFFFFFF, null, null, -1, -1);
        }

        public static FluidEntry make(
                String name, ResourceLocation stillTex, ResourceLocation flowingTex,
                Consumer<FluidType.Properties> buildAttributes
        ) {
            return make(name, 0, stillTex, flowingTex, buildAttributes, ImmutableList.of(), 0xFFFFFFFF, null, null, -1, -1);
        }

        public static FluidEntry make(
                String name, int burnTime,
                ResourceLocation stillTex, ResourceLocation flowingTex
        ) {
            return make(name, burnTime, stillTex, flowingTex, null, ImmutableList.of(), 0xFFFFFFFF, null, null, -1, -1);
        }

        public static FluidEntry make(
                String name, int burnTime,
                ResourceLocation stillTex, ResourceLocation flowingTex,
                @Nullable Consumer<FluidType.Properties> buildAttributes,
                List<Property<?>> properties,
                int tintColor,
                @Nullable ResourceLocation renderOverlay,
                @Nullable Vector3f fogColor,
                float fogStart,
                float fogEnd
        ) {
            return make(
                    name, burnTime, stillTex, flowingTex,
                    ICFluid::new, ICFluid.Flowing::new,
                    buildAttributes, properties,
                    tintColor, renderOverlay, fogColor, fogStart, fogEnd
            );
        }

        public static FluidEntry make(
                String name, int burnTime,
                ResourceLocation stillTex, ResourceLocation flowingTex,
                Function<FluidEntry, ? extends ICFluid> makeStill,
                Function<FluidEntry, ? extends ICFluid> makeFlowing,
                @Nullable Consumer<FluidType.Properties> buildAttributes,
                List<Property<?>> properties,
                int tintColor,
                @Nullable ResourceLocation renderOverlay,
                @Nullable Vector3f fogColor,
                float fogStart,
                float fogEnd
        ) {
            FluidType.Properties builder = FluidType.Properties.create()
                    .descriptionId("fluid.immersivecooking." + name)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
            if (buildAttributes != null)
                buildAttributes.accept(builder);

            DeferredHolder<FluidType, FluidType> type = ICRegisters.FLUID_TYPE_REGISTER.register(
                    name, () -> makeTypeWithTextures(builder, stillTex, flowingTex, tintColor, renderOverlay, fogColor, fogStart, fogEnd)
            );

            Mutable<FluidEntry> thisMutable = new MutableObject<>();
            ICFluid stillRaw = ICFluid.makeFluid(makeStill, thisMutable.getValue());
            DeferredHolder<Fluid, ICFluid> still = ICRegisters.FLUID_REGISTER.register(
                    name, () -> stillRaw
            );
            DeferredHolder<Fluid, ICFluid> flowing = ICRegisters.FLUID_REGISTER.register(
                    name + "_flowing", () -> ICFluid.makeFluid(makeFlowing, thisMutable.getValue())
            );

            DeferredHolder<Block, ICFluidBlock> block = ICRegisters.BLOCK_REGISTER.register(
                    name + "_fluid_block",
                    () -> new ICFluidBlock(thisMutable.getValue(), Properties.ofFullCopy(Blocks.WATER))
            );

            DeferredHolder<Item, BucketItem> bucket = ICRegisters.registerItem(
                    name + "_bucket", () -> makeBucket(stillRaw, burnTime)
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
                int tintColor,
                @Nullable ResourceLocation renderOverlay,
                @Nullable Vector3f fogColor,
                float fogStart,
                float fogEnd
        ) {
            final FluidType fluidType = new FluidType(builder);
            FLUID_TYPE_EXTENSIONS.put(new IClientFluidTypeExtensions() {
                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return stillTex;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return flowingTex;
                }

                @Override
                public int getTintColor() {
                    return tintColor;
                }

                @Override
                public @Nullable ResourceLocation getRenderOverlayTexture(@NotNull Minecraft mc) {
                    return renderOverlay;
                }

                @Override
                public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                    return fogColor != null ? fogColor : fluidFogColor;
                }

                @Override
                public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, @NotNull FogShape shape) {
                    if (fogStart >= 0 && fogEnd >= 0) {
                        RenderSystem.setShaderFogStart(fogStart);
                        RenderSystem.setShaderFogEnd(fogEnd);
                    }
                }
            }, fluidType);
            return fluidType;
        }

        private static BucketItem makeBucket(Fluid still, int burnTime) {
            return new BucketItem(
                    still, new Item.Properties()
                    .stacksTo(1)
                    .craftRemainder(Items.BUCKET)
            ) {
                @Override
                public int getBurnTime(@NotNull ItemStack itemStack, RecipeType<?> type) {
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

        public DeferredHolder<Fluid, ICFluid> getStillGetter() {
            return still;
        }
    }
}