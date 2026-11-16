package uk.akkiserver.immersivecooking.common.block.multiblock.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import uk.akkiserver.immersivecooking.common.block.multiblock.MultiblockShape;
import java.util.List;
import java.util.Map;

/** Block-local bounds from the aligned OBJ; rotated details use bounding boxes. */
public class TunnelOvenShape extends MultiblockShape {
    public static final TunnelOvenShape SHAPE_GETTER = new TunnelOvenShape();
    private static final Map<BlockPos, List<AABB>> PARTS = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 0, 3), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 0, 4), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1))),
            Map.entry(new BlockPos(0, 1, 0), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 1, 1), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 1, 2), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(0, 1, 3), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.0625, 0.5, 0.0625, 0.9375, 1, 0.9375),
                    new AABB(0.8125, 0.5, 0, 1, 1, 0.1875),
                    new AABB(0, 0.5, 0, 0.1875, 1, 0.1875),
                    new AABB(0, 0.5, 0.8125, 0.1875, 1, 1),
                    new AABB(0.8125, 0.5, 0.8125, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 3), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 0, 4), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.0625, 0.5, 0.0625, 0.9375, 1, 0.9375),
                    new AABB(0.8125, 0.5, 0, 1, 1, 0.1875),
                    new AABB(0.8125, 0.5, 0.8125, 1, 1, 1),
                    new AABB(0, 0.5, 0, 0.1875, 1, 0.1875),
                    new AABB(0, 0.5, 0.8125, 0.1875, 1, 1))),
            Map.entry(new BlockPos(1, 1, 0), List.of(
                    new AABB(0, 0.1875, 0.875, 0.0625, 0.8125, 1),
                    new AABB(0, 0.8125, 0.875, 1, 1, 1),
                    new AABB(0.9375, 0.1875, 0.875, 1, 0.8125, 1),
                    new AABB(0.125, 0.25, 0.90625, 0.25, 0.8125, 0.9375),
                    new AABB(0.28125, 0.25, 0.90625, 0.40625, 0.8125, 0.9375),
                    new AABB(0.4375, 0.25, 0.90625, 0.5625, 0.8125, 0.9375),
                    new AABB(0.59375, 0.25, 0.90625, 0.71875, 0.8125, 0.9375),
                    new AABB(0.75, 0.25, 0.90625, 0.875, 0.8125, 0.9375),
                    new AABB(0.0625, 0, 0, 0.9375, 0.125, 1),
                    new AABB(0.9375, 0, 0, 1, 0.1875, 1),
                    new AABB(0, 0, 0, 0.0625, 0.1875, 1))),
            Map.entry(new BlockPos(1, 1, 1), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 1, 2), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 1, 3), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(1, 1, 4), List.of(
                    new AABB(0, 0.8125, 0, 1, 1, 0.125),
                    new AABB(0.9375, 0.1875, 0, 1, 0.8125, 0.125),
                    new AABB(0, 0.1875, 0, 0.0625, 0.8125, 0.125),
                    new AABB(0.125, 0.25, 0.0625, 0.25, 0.8125, 0.09375),
                    new AABB(0.28125, 0.25, 0.0625, 0.40625, 0.8125, 0.09375),
                    new AABB(0.4375, 0.25, 0.0625, 0.5625, 0.8125, 0.09375),
                    new AABB(0.59375, 0.25, 0.0625, 0.71875, 0.8125, 0.09375),
                    new AABB(0.75, 0.25, 0.0625, 0.875, 0.8125, 0.09375),
                    new AABB(0.0625, 0, 0, 0.9375, 0.125, 1),
                    new AABB(0.9375, 0, 0, 1, 0.1875, 1),
                    new AABB(0, 0, 0, 0.0625, 0.1875, 1))),
            Map.entry(new BlockPos(2, 0, 0), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1))),
            Map.entry(new BlockPos(2, 0, 1), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 0, 2), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 0, 3), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0, 0.5, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 0, 4), List.of(
                    new AABB(0, 0, 0, 1, 0.5, 1),
                    new AABB(0.625, 0.5, 0.75, 0.875, 1, 0.875),
                    new AABB(0.625, 0.5, 0.125, 0.875, 1, 0.25))),
            Map.entry(new BlockPos(2, 1, 1), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 1, 2), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 1, 3), List.of(
                    new AABB(0, 0, 0, 1, 1, 1))),
            Map.entry(new BlockPos(2, 1, 4), List.of(
                    new AABB(0.5, 0, 0, 1, 1, 1))));

    @Override
    protected List<AABB> getShape(BlockPos position) {
        return PARTS.getOrDefault(position, List.of());
    }
}
