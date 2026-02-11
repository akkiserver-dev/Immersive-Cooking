package uk.akkiserver.immersivecooking.data.blockstates;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.data.blockstates.ExtendedBlockstateProvider;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static blusunrize.immersiveengineering.ImmersiveEngineering.rl;

public class ICBlockStates extends ExtendedBlockstateProvider {
    private static final List<Vec3i> CUBE_THREE = BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
            .map(BlockPos::immutable)
            .collect(Collectors.toList());
    public ModelFile grillOvenOff;
    public ModelFile grillOvenOn;
    public final Map<Block, ModelFile> unsplitModels = new HashMap<>();

    public ICBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        grillOvenOff = cubeThree("grill_oven_off",
                Resource.mod("block/multiblock/grill_oven"),
                Resource.mod("block/multiblock/grill_oven_off")
        );

        grillOvenOn = cubeThree("grill_oven_on",
                Resource.mod("block/multiblock/grill_oven"),
                Resource.mod("block/multiblock/grill_oven_on")
        );

        createMultiblock(
                ICContent.Multiblock.GRILL_OVEN.block(),
                grillOvenOff,
                grillOvenOn,
                IEProperties.ACTIVE
        );
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel, @Nullable Property<Boolean> mirroredState) {
        createMultiblock(b, masterModel, mirroredModel, IEProperties.FACING_HORIZONTAL, mirroredState);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState) {
        unsplitModels.put(b.get(), masterModel);
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b.get());
        boolean[] possibleMirrorStates;
        if (mirroredState != null) {
            possibleMirrorStates = new boolean[]{false, true};
        } else {
            possibleMirrorStates = new boolean[1];
        }
        for (boolean mirrored : possibleMirrorStates) {
            for (Direction dir : facing.getPossibleValues()) {
                final int angleY;
                final int angleX;
                if (facing.getPossibleValues().contains(Direction.UP)) {
                    angleX = -90 * dir.getStepY();
                    if (dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, 180);
                    else
                        angleY = 0;
                } else {
                    angleY = getAngle(dir, 180);
                    angleX = 0;
                }
                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
                        .with(facing, dir);
                if (mirroredState != null)
                    partialState = partialState.with(mirroredState, mirrored);
                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
        }
    }

    private ModelFile cubeThree(String name, ResourceLocation def, ResourceLocation front) {
        NongeneratedModels.NongeneratedModel baseModel = obj(name, rl("block/stone_multiblocks/cube_three.obj"),
                ImmutableMap.of("side", def, "front", front), innerModels);
        return splitModel(name + "_split", baseModel, CUBE_THREE, false);
    }

    @Override
    public @NotNull String getName() {
        return "Blockstates";
    }
}