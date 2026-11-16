package uk.akkiserver.immersivecooking.common.block.multiblock.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;
import uk.akkiserver.immersivecooking.common.block.multiblock.shape.TunnelOvenShape;

import java.util.function.Function;

/** Static assembly prototype; no inventory, energy or processing is exposed. */
public class TunnelOvenLogic implements IMultiblockLogic<TunnelOvenLogic.State> {
    @Override
    public State createInitialState(IInitialMultiblockContext<State> context) {
        return new State();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType type) {
        return TunnelOvenShape.SHAPE_GETTER;
    }

    public static class State implements IMultiblockState {
        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        }
    }
}
