package uk.akkiserver.immersivecooking.common.blocks.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.MultiblockShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FoodFermenterShape extends MultiblockShape {
    public static final FoodFermenterShape SHAPE_GETTER = new FoodFermenterShape();

    private static final AABB FULL = new AABB(0, 0, 0, 1, 1, 1);
    private static final AABB HALF_BOTTOM = new AABB(0, 0, 0, 1, .5, 1);

    private static final List<AABB> BODY_SHAPE = List.of(
            new AABB(-1.4375, 0.125, -1.4375, 0.4375, 2, 0.4375),
            new AABB(-1.5, 0.125, 0.125 - 0.5, -1.4375, 0.875, 0.875 - 0.5),
            new AABB(-1.5, 0.9375, 0.375 - 0.5, -1.4375, 1.1875, 0.625 - 0.5),
            new AABB(-1.5, 0.75, -0.625 - 0.5, -1.4375, 1, -0.375 - 0.5),
            new AABB(-1.5, 0.1875, -0.75 - 0.5, -1.4375, 0.6875, -0.25 - 0.5),
            new AABB(-0.75 - 0.5, 0.1875, -1.5, -0.25 - 0.5, 0.6875, -1.4375),
            new AABB(-0.625 - 0.5, 0.75, -1.5, -0.375 - 0.5, 1, -1.4375),
            new AABB(-1.5, 0, -1.5, 0.5, 0.125, 0.5),
            new AABB(0.4375, 0.9375, -0.125 - 0.5, 0.5, 1.1875, 0.125 - 0.5));

    // private static final List<AABB> PIPE_SHAPE = List.of(
    // new AABB(-0.5, 0, -0.125, 0.125, 0.25, 0.125),
    // new AABB(-0.5, 0.9375, -0.625, -0.25, 1.1875, 0.125),
    // new AABB(-0.5, 0.25, -0.125, -0.25, 0.9375, 0.125)
    // );

    private static final List<AABB> PIPE_SHAPE = List.of(
            new AABB(-1.0, 0, -0.125, -0.375, 0.25, 0.125),
            new AABB(-1.0, 0.9375, -0.625, -0.75, 1.1875, 0.125),
            new AABB(-1.0, 0.25, -0.125, -0.75, 0.9375, 0.125));

    @Override
    protected @NotNull List<AABB> getShape(BlockPos posInMultiblock) {
        if (new BlockPos(2, 0, 2).equals(posInMultiblock)) {
            return new ArrayList<>(List.of(
                    HALF_BOTTOM,
                    new AABB(0.125, .5, 0.625, 0.25, 1, 0.875),
                    new AABB(0.75, .5, 0.625, 0.875, 1, 0.875)));
        }

        if (new BoundingBox(0, 0, 0, 1, 0, 1).isInside(posInMultiblock)) {
            List<AABB> list = new ArrayList<>();
            list.add(HALF_BOTTOM);
            list.add(new AABB(0.0625, .5, 0.6875, 0.3125, 1, 0.9375));

            if (new BlockPos(1, 0, 1).equals(posInMultiblock)) {
                list.add(new AABB(0, .5, 0.375, 1.125, .75, 0.625));
                list.add(new AABB(0.875, .5, -0.125, 1.125, .75, 0.375));
                list.add(new AABB(0.875, .75, -0.125, 1.125, 1, 0.125));
            }

            return list;
        }

        // パイプ部分 (参照ブロック(2,1,1), xはオフセットなし, zは-0.5)
        if (new BoundingBox(2, 1, 1, 2, 2, 1).isInside(posInMultiblock)) {
            List<AABB> list = new ArrayList<>();

            double globalMinX = (posInMultiblock.getX() - 2) - 1.0;
            double globalMaxX = globalMinX + 1.0;
            double globalMinY = (posInMultiblock.getY() - 1);
            double globalMaxY = globalMinY + 1.0;
            double globalMinZ = (posInMultiblock.getZ() - 1) - 0.5;
            double globalMaxZ = globalMinZ + 1.0;

            AABB blockBounds = new AABB(globalMinX, globalMinY, globalMinZ, globalMaxX, globalMaxY, globalMaxZ);

            for (AABB box : PIPE_SHAPE) {
                if (box.intersects(blockBounds)) {
                    AABB intersection = box.intersect(blockBounds);
                    list.add(intersection.move(-globalMinX, -globalMinY, -globalMinZ));
                }
            }

            return list;
        }

        // 本体部分 Y=1-2 (X=0-1, Z=0-1), 参照ブロック(1,1,1), xz-0.5
        if (new BoundingBox(0, 1, 0, 1, 2, 1).isInside(posInMultiblock)) {
            List<AABB> list = new ArrayList<>();

            double globalMinX = (posInMultiblock.getX() - 1) - 0.5;
            double globalMaxX = globalMinX + 1.0;
            double globalMinY = (posInMultiblock.getY() - 1);
            double globalMaxY = globalMinY + 1.0;
            double globalMinZ = (posInMultiblock.getZ() - 1) - 0.5;
            double globalMaxZ = globalMinZ + 1.0;

            AABB blockBounds = new AABB(globalMinX, globalMinY, globalMinZ, globalMaxX, globalMaxY, globalMaxZ);

            for (AABB box : BODY_SHAPE) {
                if (box.intersects(blockBounds)) {
                    AABB intersection = box.intersect(blockBounds);
                    list.add(intersection.move(-globalMinX, -globalMinY, -globalMinZ));
                }
            }

            return list;
        }

        if (new BlockPos(2, 1, 2).equals(posInMultiblock))
            return new ArrayList<>(List.of(new AABB(0, 0, 0.5, 1, 1, 1)));

        if (posInMultiblock.getY() == 0 && !Set.of(
                new BlockPos(0, 0, 0),
                new BlockPos(2, 0, 1)).contains(posInMultiblock)) {
            return new ArrayList<>(List.of(HALF_BOTTOM));
        }

        return new ArrayList<>(List.of(FULL));
    }
}