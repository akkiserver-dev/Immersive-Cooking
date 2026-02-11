package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic.State;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class GrillOvenLogic implements IMultiblockLogic<State>, IServerTickableComponent<State> {
    public static final BlockPos MASTER_OFFSET = new BlockPos(1, 1, 1);
    private static final RecipeManager.CachedCheck<Container, SmokingRecipe> smokingChecker = RecipeManager.createCheck(RecipeType.SMOKING);

    public static final int IO_SLOT_0 = 0;
    public static final int IO_SLOT_1 = 1;
    public static final int IO_SLOT_2 = 2;
    public static final int FUEL_SLOT = 3;
    public static final int NUM_SLOTS = 4;
    public static final int DATA_SLOTS = 8;

    public GrillOvenLogic() {

    }

    public synchronized static Optional<SmokingRecipe> findRecipe(ItemStack stack, Level level) {
        if (stack.isEmpty()) return Optional.empty();

        return smokingChecker.getRecipeFor(new SimpleContainer(stack), level);
    }

    @Nullable
    public SmokingRecipe getRecipe(IMultiblockContext<State> ctx, int slot) {
        final State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();
        if (slot < 0 || slot > 2) return null;

        ItemStack input = state.inventory.getStackInSlot(slot);

        return findRecipe(input, level).orElse(null);
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
                int burnTime = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMOKING);
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
                        state.processes[i] = Math.max(1, (int)(recipe.getCookingTime() * inputStack.getCount() / 2.0));
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
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap) {
        State state = ctx.getState();
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return state.invCap.cast(ctx);
        } else {
            return LazyOptional.empty();
        }
    }

    private void finishCooking(State state, Level level, int slot, SmokingRecipe recipe) {
        ItemStack currentOutput = state.inventory.getStackInSlot(slot);
        int itemSize = currentOutput.getCount();
        ItemStack result = recipe.getResultItem(level.registryAccess()).copyWithCount(itemSize);

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

    public static class State implements IMultiblockState, ContainerData {
        private final SlotwiseItemHandler inventory;
        private final StoredCapability<IItemHandler> invCap;
        public int burnTime;
        public int maxBurnTime;
        public int[] processes = new int[3];
        public int[] processMaxes = new int[3];

        public State(IInitialMultiblockContext<State> ctx) {
            inventory = new SlotwiseItemHandler(
                    List.of(
                            // Oven can input any item, but if the item does not any smoking recipe, so a process doesn't start
                            SlotwiseItemHandler.IOConstraint.input($ -> true),
                            SlotwiseItemHandler.IOConstraint.input($ -> true),
                            SlotwiseItemHandler.IOConstraint.input($ -> true),
                            SlotwiseItemHandler.IOConstraint.input(stack -> ForgeHooks.getBurnTime(stack, RecipeType.SMOKING) > 0)
                    ),
                    ctx.getMarkDirtyRunnable()
            );
            this.invCap = new StoredCapability<>(this.inventory);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt) {
            nbt.putInt("burnTime", burnTime);
            nbt.putInt("maxBurnTime", maxBurnTime);
            nbt.putIntArray("process", processes);
            nbt.putIntArray("processMax", processMaxes);
            nbt.put("inventory", inventory.serializeNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt) {
            burnTime = nbt.getInt("burnTime");
            maxBurnTime = nbt.getInt("maxBurnTime");
            processes = nbt.getIntArray("process");
            processMaxes = nbt.getIntArray("processMax");
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            processes = (processes.length == 3) ? processes : new int[3];
            processMaxes = (processMaxes.length == 3) ? processMaxes : new int[3];
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
