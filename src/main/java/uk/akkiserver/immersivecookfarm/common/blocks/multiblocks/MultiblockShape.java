package uk.akkiserver.immersivecookfarm.common.blocks.multiblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

//
// this class from 'Immersive Petroleum's GenericShape, thanks to original mod authors!
//
public abstract class MultiblockShape implements Function<BlockPos, VoxelShape> {
    private static VoxelShape toVoxelShape(AABB aabb) {
        return Shapes.box(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    @Override
    public VoxelShape apply(BlockPos posInMultiblock){
        List<AABB> list = getShape(posInMultiblock);

        VoxelShape shape;
        if (list.size() > 1) {
            VoxelShape base = toVoxelShape(list.remove(0));
            shape = list.stream().map(MultiblockShape::toVoxelShape).reduce(base, Shapes::or);
        } else {
            if (!list.isEmpty()) {
                shape = toVoxelShape(list.remove(0));
            } else {
                shape = Shapes.empty();
            }
        }
        return shape;
    }

    @Nonnull
    protected abstract List<AABB> getShape(BlockPos posInMultiblock);
}
