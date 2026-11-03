package uk.akkiserver.immersivecooking.common.block.multiblock.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import uk.akkiserver.immersivecooking.common.block.multiblock.MultiblockShape;
import java.util.List;
import java.util.Map;

/**
 * Block-local boxes clipped from the unrotated cuboids in slicer.bbmodel.
 * Template coordinates are model coordinates + (1, 0, 1) blocks.
 * IE applies facing and mirroring to these shapes.
 */
public class SlicerShape extends MultiblockShape {
    public static final SlicerShape SHAPE_GETTER = new SlicerShape();
    private static final Map<BlockPos, List<AABB>> PARTS = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1))),
            Map.entry(new BlockPos(0, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 1, 1), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 1, 2), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 0.1875, 0.8125, 0.1875),
                    new AABB(0.8125, 0.5, 0.9375, 1, 0.8125, 1),
                    new AABB(0.8125, 0.5, 0, 1, 0.8125, 0.1875),
                    new AABB(0.0625, 0.5, 0.0625, 0.9375, 0.8125, 1),
                    new AABB(0, 0.8125, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    // The RS panel is a zero-thickness model face; give it one pixel of inward depth.
                    new AABB(0.9375, 0, 0.125, 1, 1, 0.875),
                    new AABB(0.8125, 0.5, 0.875, 1, 0.8125, 1),
                    new AABB(0.8125, 0.5, 0, 1, 0.8125, 0.125),
                    new AABB(0.0625, 0.5, 0, 0.9375, 0.8125, 1),
                    new AABB(0, 0.8125, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.8125, 0.5, 0.8125, 1, 0.8125, 1),
                    new AABB(0.8125, 0.5, 0, 1, 0.8125, 0.0625),
                    new AABB(0, 0.5, 0.8125, 0.1875, 0.8125, 1),
                    new AABB(0.0625, 0.5, 0, 0.9375, 0.8125, 0.9375),
                    new AABB(0, 0.8125, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 1, 0), List.of(
                    new AABB(0, 0, 0, 0.0625, 0.1875, 1),
                    new AABB(0.9375, 0, 0, 1, 0.1875, 1),
                    new AABB(0.0625, 0, 0, 0.9375, 0.125, 1))),
            Map.entry(new BlockPos(1, 1, 1), List.of(
                    new AABB(0, 0, 0, 0.0625, 0.1875, 1),
                    new AABB(0.9375, 0, 0, 1, 0.1875, 1),
                    new AABB(0.0625, 0, 0, 0.9375, 0.125, 1),
                    new AABB(0.125, 0.125, 0.1875, 0.875, 0.6875, 0.8125),
                    new AABB(0.179688, 0.25, 0.8125, 0.304688, 0.6875, 0.84375),
                    new AABB(0.351562, 0.25, 0.8125, 0.476562, 0.6875, 0.84375),
                    new AABB(0.523438, 0.25, 0.8125, 0.648438, 0.6875, 0.84375),
                    new AABB(0.695312, 0.25, 0.8125, 0.820312, 0.6875, 0.84375),
                    new AABB(0.179688, 0.25, 0.15625, 0.304688, 0.6875, 0.1875),
                    new AABB(0.351562, 0.25, 0.15625, 0.476562, 0.6875, 0.1875),
                    new AABB(0.523438, 0.25, 0.15625, 0.648438, 0.6875, 0.1875),
                    new AABB(0.695312, 0.25, 0.15625, 0.820312, 0.6875, 0.1875),
                    new AABB(0, 0.6875, 0.125, 1, 0.8125, 0.875),
                    new AABB(0, 0.125, 0.125, 0.125, 0.6875, 0.875),
                    new AABB(0.875, 0.125, 0.125, 1, 0.6875, 0.875),
                    new AABB(0, 0.8125, 0.1875, 0.375, 1, 0.4375),
                    new AABB(0, 0.8125, 0.5, 0.75, 0.875, 0.6875))),
            Map.entry(new BlockPos(1, 1, 2), List.of(
                    new AABB(0.0625, 0, 0, 0.9375, 0.125, 1),
                    new AABB(0, 0, 0, 0.0625, 0.1875, 1),
                    new AABB(0.9375, 0, 0, 1, 0.1875, 1))));

    @Override
    protected List<AABB> getShape(BlockPos position) {
        return PARTS.getOrDefault(position, List.of());
    }
}
