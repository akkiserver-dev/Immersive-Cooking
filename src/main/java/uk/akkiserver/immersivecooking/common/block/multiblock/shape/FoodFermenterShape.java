package uk.akkiserver.immersivecooking.common.block.multiblock.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.block.multiblock.MultiblockShape;

import java.util.List;
import java.util.Map;

// Generated from src/main/resources/assets/immersivecooking/models/block/multiblock/food_fermenter.obj.
// Regenerate with: node tools/generate-multiblock-shapes.mjs
// Model coordinates + master (1, 1, 1) are split into template cells.
// Overhangs in template air are retained on the nearest formed cell.
// Immersive Engineering applies facing and mirroring to these block-local boxes.
public class FoodFermenterShape extends MultiblockShape {
    public static final FoodFermenterShape SHAPE_GETTER = new FoodFermenterShape();

    private static final Map<BlockPos, List<AABB>> PARTS = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.0625, 0.5, 0.0625, 0.3125, 1, 0.3125))),
            Map.entry(new BlockPos(0, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.0625, 0.5, 0.6875, 0.3125, 1, 0.9375))),
            Map.entry(new BlockPos(0, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 1, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.125, 1),
                    new AABB(0, 0.1875, 0.25, 0.0625, 0.6875, 0.75),
                    new AABB(0, 0.75, 0.375, 0.0625, 1, 0.625),
                    new AABB(0.0625, 0.125, 0.0625, 1, 1, 1),
                    new AABB(0.25, 0.1875, 0, 0.75, 0.6875, 0.0625),
                    new AABB(0.375, 0.75, 0, 0.625, 1, 0.0625))),
            Map.entry(new BlockPos(0, 1, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.125, 1),
                    new AABB(0, 0.125, 0.125, 0.0625, 0.875, 0.875),
                    new AABB(0, 0.9375, 0.375, 0.0625, 1, 0.625),
                    new AABB(0.0625, 0.125, 0, 1, 1, 0.9375))),
            Map.entry(new BlockPos(0, 1, 2), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 2, 0), List.of(
                    new AABB(0.0625, 0, 0.0625, 1, 1, 1))),
            Map.entry(new BlockPos(0, 2, 1), List.of(
                    new AABB(0, 0, 0.375, 0.0625, 0.1875, 0.625),
                    new AABB(0.0625, 0, 0, 1, 1, 0.9375))),
            Map.entry(new BlockPos(1, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.6875, 0.5, 0.0625, 0.9375, 1, 0.3125))),
            Map.entry(new BlockPos(1, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.6875, 0.5, 0.6875, 0.9375, 1, 0.9375))),
            Map.entry(new BlockPos(1, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1))),
            Map.entry(new BlockPos(1, 1, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.125, 1),
                    new AABB(0, 0.125, 0.0625, 0.9375, 1, 1),
                    new AABB(0.9375, 0.9375, 0.875, 1, 1, 1),
                    new AABB(1, 0.9375, 0.875, 1.25, 1, 1))),
            Map.entry(new BlockPos(1, 1, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.125, 1),
                    new AABB(0, 0.125, 0, 0.9375, 1, 0.9375),
                    new AABB(0.9375, 0.9375, 0, 1, 1, 0.125))),
            Map.entry(new BlockPos(1, 2, 0), List.of(
                    new AABB(0, 0, 0.0625, 0.9375, 1, 1),
                    new AABB(0.9375, 0, 0.875, 1, 0.1875, 1),
                    new AABB(1, 0, 0.875, 1.25, 0.1875, 1))),
            Map.entry(new BlockPos(1, 2, 1), List.of(
                    new AABB(0, 0, 0, 0.9375, 1, 0.9375),
                    new AABB(0.9375, 0, 0, 1, 0.1875, 0.125))),
            Map.entry(new BlockPos(2, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1))),
            Map.entry(new BlockPos(2, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.125, 0.5, 0.625, 0.25, 1, 0.875),
                    new AABB(0.75, 0.5, 0.625, 0.875, 1, 0.875))),
            Map.entry(new BlockPos(2, 1, 1), List.of(
                    new AABB(0, 0, 0.375, 0.625, 0.25, 0.625),
                    new AABB(0, 0.25, 0.375, 0.25, 0.9375, 0.625),
                    new AABB(0, 0.9375, 0, 0.25, 1, 0.625))),
            Map.entry(new BlockPos(2, 1, 2), List.of(
                    new AABB(0, 0, 0.5, 1, 1, 1))),
            Map.entry(new BlockPos(2, 2, 1), List.of(
                    new AABB(0, 0, 0, 0.25, 0.1875, 0.625))));

    @Override
    protected @NotNull List<AABB> getShape(BlockPos posInMultiblock) {
        return PARTS.getOrDefault(posInMultiblock, List.of());
    }
}
