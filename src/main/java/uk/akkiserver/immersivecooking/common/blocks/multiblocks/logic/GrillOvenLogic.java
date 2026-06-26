package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic.State;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class GrillOvenLogic implements IMultiblockLogic<State>, IServerTickableComponent<State> {
    public static final BlockPos MASTER_OFFSET = new BlockPos(1, 1, 1);

    public static final int IO_SLOT_0 = 0;
    public static final int IO_SLOT_1 = 1;
    public static final int IO_SLOT_2 = 2;
    public static final int FUEL_SLOT = 3;
    public static final int NUM_SLOTS = 4;
    public static final int DATA_SLOTS = 8;

    @Nullable
    public SmokingRecipe getRecipe(IMultiblockContext<State> ctx, int slot) {
        final State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();
        if (slot < 0 || slot > 2) return null;

        ItemStack input = state.inventory.getStackInSlot(slot);
        if (input.isEmpty()) return null;

        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMOKING, new SingleRecipeInput(input), level)
                .map(RecipeHolder::value)
                .orElse(null);
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        final Level level = context.getLevel().getRawLevel();
        boolean dirty = false;

        boolean hasWork = false;
        for (int i = 0; i < 3; i++) {
            if (!state.inventory.getStackInSlot(i).isEmpty() && getRecipe(context, i) != null) {
                hasWork = true;
                break;
            }
        }

        if (state.burnTime <= 0 && hasWork) {
            ItemStack fuelStack = state.inventory.getStackInSlot(3);
            if (!fuelStack.isEmpty()) {
                int burnTime = fuelStack.getBurnTime(RecipeType.SMOKING);
                if (burnTime > 0) {
                    state.maxBurnTime = burnTime;
                    state.burnTime = burnTime;
                    fuelStack.shrink(1);
                    dirty = true;
                }
            }
        }

        if (state.burnTime > 0) {
            state.burnTime--;
            dirty = true;

            for (int i = 0; i < 3; i++) {
                ItemStack inputStack = state.inventory.getStackInSlot(i);
                if (inputStack.isEmpty()) {
                    state.processes[i] = 0;
                    continue;
                }

                SmokingRecipe recipe = getRecipe(context, i);
                if (recipe != null) {
                    if (state.processes[i] <= 0) {
                        state.processes[i] = Math.max(1, (int) (recipe.getCookingTime() * inputStack.getCount() / 2.0));
                        state.processMaxes[i] = state.processes[i];
                    } else {
                        state.processes[i]--;
                        if (state.processes[i] <= 0) {
                            finishCooking(state, level, i, recipe);
                        }
                    }
                } else {
                    state.processes[i] = 0;
                }
            }
        }

        if (dirty) {
            context.markMasterDirty();
        }
    }

    @Override
    public void registerCapabilities(CapabilityRegistrar<State> register) {
        register.registerEverywhere(Capabilities.ItemHandler.BLOCK, state -> state.inventory);
    }

    private void finishCooking(State state, Level level, int slot, SmokingRecipe recipe) {
        ItemStack currentOutput = state.inventory.getStackInSlot(slot);
        int itemSize = currentOutput.getCount();
        ItemStack result = recipe.assemble(new SingleRecipeInput(currentOutput), level.registryAccess()).copyWithCount(itemSize);

        currentOutput.shrink(itemSize);

        if (currentOutput.isEmpty()) {
            state.inventory.setStackInSlot(slot, result);
        } else {
            currentOutput.grow(result.getCount());
        }
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop) {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capabilitySource) {
        return new State(capabilitySource);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return $ -> Shapes.block();
    }

    public Optional<RecipeHolder<SmokingRecipe>> findRecipe(SingleRecipeInput input, Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, input, level);
    }

    public static class State implements ContainerData, IMultiblockState {
        private final SlotwiseItemHandler inventory;
        public int burnTime;
        public int maxBurnTime;
        public int[] processes = new int[3];
        public int[] processMaxes = new int[3];

        public State(IInitialMultiblockContext<State> ctx) {
            inventory = SlotwiseItemHandler.makeWithGroups(
                    List.of(
                            // Oven can input any item, but if the item does not match any smoking recipe, no process starts
                            new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.input($ -> true), 3),
                            new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.input(stack -> stack.getBurnTime(RecipeType.SMOKING) > 0), 1)
                    ),
                    ctx.getMarkDirtyRunnable()
            );
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.putInt("burnTime", burnTime);
            nbt.putInt("maxBurnTime", maxBurnTime);
            nbt.putIntArray("process", processes);
            nbt.putIntArray("processMax", processMaxes);
            nbt.put("inventory", inventory.serializeNBT(provider));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            burnTime = nbt.getInt("burnTime");
            maxBurnTime = nbt.getInt("maxBurnTime");
            processes = nbt.getIntArray("process");
            processMaxes = nbt.getIntArray("processMax");
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            processes = (processes.length == 3) ? processes : new int[3];
            processMaxes = (processMaxes.length == 3) ? processMaxes : new int[3];
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            writeSaveNBT(nbt, provider);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            readSaveNBT(nbt, provider);
        }

        public SlotwiseItemHandler getInventory() {
            return inventory;
        }

        @Override
        public int get(int index) {
            if (index < 3) return processes[index];
            if (index < 6) return processMaxes[index - 3];
            if (index == 6) return burnTime;
            if (index == 7) return maxBurnTime;
            throw new IllegalArgumentException("Unknown index: " + index);
        }

        @Override
        public void set(int index, int value) {
            if (index < 3) processes[index] = value;
            else if (index < 6) processMaxes[index - 3] = value;
            else if (index == 6) burnTime = value;
            else if (index == 7) maxBurnTime = value;
        }

        @Override
        public int getCount() {
            return DATA_SLOTS;
        }
    }
}