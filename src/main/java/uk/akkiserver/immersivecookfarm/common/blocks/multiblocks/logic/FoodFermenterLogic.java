package uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.phys.shapes.VoxelShape;
import uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.logic.FoodFermenterLogic.State;
import uk.akkiserver.immersivecookfarm.common.crafting.FoodFermenterRecipe;

import java.util.function.Function;

public class FoodFermenterLogic extends ICMultiblockLogic<State, FoodFermenterRecipe> implements IServerTickableComponent<State>, IClientTickableComponent<State> {

    @Override
    public void tickClient(IMultiblockContext<State> context) {

    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {

    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capabilitySource) {
        return null;
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return null;
    }

    public static class State implements ContainerData, IMultiblockState {

        @Override
        public void writeSaveNBT(CompoundTag nbt) {

        }

        @Override
        public void readSaveNBT(CompoundTag nbt) {

        }

        @Override
        public int get(int pIndex) {
            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {

        }

        @Override
        public int getCount() {
            return 0;
        }
    }
}
