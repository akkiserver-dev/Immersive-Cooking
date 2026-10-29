package uk.akkiserver.immersivecooking.common.block.multiblock.shape;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.Test;
import uk.akkiserver.immersivecooking.common.block.multiblock.MultiblockShape;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiblockShapeTest {
    private static final BlockPos MASTER = new BlockPos(1, 1, 1);
    private static final Direction[] FACINGS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    @Test
    void cookpotSolidAndEmptyFeaturesFollowAllEightIeOrientations() {
        assertProbes(CookpotShape.SHAPE_GETTER, List.of(
                new Probe("northwest foot", -13, -4, -13, true),
                new Probe("northeast foot", 13, -4, -13, true),
                new Probe("southwest foot", -13, -4, 13, true),
                new Probe("southeast foot", 13, -4, 13, true),
                new Probe("gap beside feet", -13, -4, -1, false),
                new Probe("hollow pot", 2, 8, 2, false),
                new Probe("pot wall", -14, 10, 5, true),
                new Probe("faucet mounting plate", 15.5, 6, 5, true),
                new Probe("faucet outlet", 23, 6, 8, true),
                new Probe("air past faucet", 29, 8, 8, false),
                new Probe("hollow lower port", 24, -8, 8, false),
                new Probe("lower port wall", 31, -8, 8, true),
                new Probe("lid handle", 0, 27, 0, true),
                new Probe("air beside handle", 12, 27, 0, false),
                new Probe("redstone block", -8, 8, 24, true),
                new Probe("empty template cell", 8, 8, 24, false)));
    }

    @Test
    void fermenterSolidAndEmptyFeaturesFollowAllEightIeOrientations() {
        assertProbes(FoodFermenterShape.SHAPE_GETTER, List.of(
                new Probe("northwest foot", -13, -4, -13, true),
                new Probe("northeast foot", 13, -4, -13, true),
                new Probe("southwest foot", -13, -4, 13, true),
                new Probe("southeast foot", 13, -4, 13, true),
                new Probe("gap beside feet", -13, -4, -1, false),
                new Probe("solid tank", 2, 8, 2, true),
                new Probe("gap between tank and pipe", 15.5, 8, 8, false),
                new Probe("resized left port", -15.5, 2.5, 13, true),
                new Probe("raised left port", -15.5, 18, 8, true),
                new Probe("gap between left ports", -15.5, 14.5, 8, false),
                new Probe("lower pipe overhang", 18, 15.5, -1, true),
                new Probe("upper pipe overhang", 18, 17, -1, true),
                new Probe("upper pipe", 18, 17, 8, true),
                new Probe("air beside pipe", 23, 8, 8, false),
                new Probe("air above pipe", 24, 24, 0, false)));
    }

    @Test
    void cookpotSelectionRayPassesThroughCavityAndHitsTheWall() {
        VoxelShape shape = CookpotShape.SHAPE_GETTER.apply(MASTER);
        assertNull(shape.clip(new Vec3(0.125, 0.9, 0.125), new Vec3(0.125, 0.25, 0.125), BlockPos.ZERO));
        var hit = shape.clip(new Vec3(0.5, 0.5, 0.125), new Vec3(1, 0.5, 0.125), BlockPos.ZERO);
        assertNotNull(hit);
        assertEquals(0.8125, hit.getLocation().x, 1e-9);
        assertEquals(Direction.WEST, hit.getDirection());
    }

    @Test
    void emptyAndOutOfTemplatePositionsHaveNoFallbackCube() {
        for (MultiblockShape shape : List.of(CookpotShape.SHAPE_GETTER, FoodFermenterShape.SHAPE_GETTER)) {
            for (BlockPos pos : List.of(new BlockPos(1, 1, 2), new BlockPos(2, 2, 2),
                    new BlockPos(-1, 1, 1), new BlockPos(3, 1, 1)))
                assertTrue(shape.apply(pos).isEmpty(), pos.toString());
        }
        assertTrue(CookpotShape.SHAPE_GETTER.apply(new BlockPos(2, 2, 1)).isEmpty());
    }

    @Test
    void repeatedQueriesDoNotConsumeImmutableGeneratedBoxes() {
        for (MultiblockShape shape : List.of(CookpotShape.SHAPE_GETTER, FoodFermenterShape.SHAPE_GETTER)) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        VoxelShape first = shape.apply(pos);
                        VoxelShape second = shape.apply(pos);
                        assertFalse(Shapes.joinIsNotEmpty(first, second, BooleanOp.NOT_SAME), pos.toString());
                    }
                }
            }
        }
    }

    private static void assertProbes(MultiblockShape shape, List<Probe> probes) {
        for (Direction facing : FACINGS) {
            for (boolean mirrored : new boolean[]{false, true}) {
                MultiblockOrientation orientation = new MultiblockOrientation(facing, mirrored);
                List<AABB> worldBoxes = new ArrayList<>();
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        for (int z = 0; z < 3; z++) {
                            BlockPos part = new BlockPos(x, y, z);
                            BlockPos offset = orientation.getAbsoluteOffset(part.subtract(MASTER));
                            for (AABB box : orientation.transformRelativeShape(shape.apply(part)).toAabbs())
                                worldBoxes.add(box.move(offset));
                        }
                    }
                }
                for (Probe probe : probes) {
                    Vec3 point = transformModelPoint(probe, facing, mirrored);
                    assertEquals(probe.solid, worldBoxes.stream().anyMatch(box -> box.contains(point)),
                            probe.name + ", facing=" + facing + ", mirrored=" + mirrored);
                }
            }
        }
    }

    // Render-model transforms rotate/mirror around the master block center.
    // This is independent of IE's per-part shape and integer-offset transforms.
    private static Vec3 transformModelPoint(Probe probe, Direction facing, boolean mirrored) {
        double x = probe.x / 16;
        double y = probe.y / 16;
        double z = probe.z / 16;
        if (mirrored)
            x = 1 - x;
        return switch (facing) {
            case NORTH -> new Vec3(x, y, z);
            case EAST -> new Vec3(1 - z, y, x);
            case SOUTH -> new Vec3(1 - x, y, 1 - z);
            case WEST -> new Vec3(z, y, 1 - x);
            default -> throw new IllegalArgumentException("Expected a horizontal direction");
        };
    }

    private record Probe(String name, double x, double y, double z, boolean solid) {
    }
}
