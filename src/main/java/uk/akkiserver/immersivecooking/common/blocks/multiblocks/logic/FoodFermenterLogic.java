package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.FoodFermenterLogic.State;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.crafting.providers.DefaultFoodFermenterRecipeProvider;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FoodFermenterLogic extends ICMultiblockLogic<State, FoodFermenterRecipe>
        implements IServerTickableComponent<State>, IClientTickableComponent<State> {
    public static final BlockPos REDSTONE_POS = new BlockPos(2, 1, 2);
    public static final MultiblockFace ITEM_OUTPUT = new MultiblockFace(3, 0, 1, RelativeBlockFace.RIGHT);
    public static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(ITEM_OUTPUT);
    public static final BlockPos ITEM_INPUT = new BlockPos(0, 1, 0);
    public static final CapabilityPosition ENERGY_POS = new CapabilityPosition(0, 1, 2, RelativeBlockFace.UP);

    public static final int TANK_CAPACITY = 12000;
    public static final int ENERGY_CAPACITY = 16000;

    public static final int NUM_INPUT_SLOTS = 6;
    public static final int EMPTY_FLUID_SLOT = 6;
    public static final int FILLED_FLUID_SLOT = 7;
    public static final int INPUT_CONTAINER_SLOT = 8;
    public static final int OUTPUT_SLOT = 9;
    public static final int NUM_SLOTS = 10;

    public FoodFermenterLogic() {
        this.recipeProviders.add(new DefaultFoodFermenterRecipeProvider());
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> ctx) {
        return new State(ctx);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position,
                                             Capability<T> cap) {
        final State state = ctx.getState();
        if (cap == ForgeCapabilities.ENERGY && ENERGY_POS.equalsOrNullFace(position)) {
            return state.energyCap.cast();
        } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (ITEM_INPUT.equals(position.posInMultiblock())) {
                return state.itemInputCap.cast();
            } else if (ITEM_OUTPUT_CAP.equals(position)) {
                return state.itemOutputCap.cast();
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public void tickServer(IMultiblockContext<State> ctx) {
        final State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();

        boolean active = state.processor.tickServer(state, ctx.getLevel(), state.rsState.isEnabled(ctx));
        if (active != state.active) {
            state.active = active;
            ctx.requestMasterBESync();
        }

        boolean update = false;

        // Fluid Input (Bucket -> Tank) logic
        ItemStack containerIn = state.inventory.getStackInSlot(EMPTY_FLUID_SLOT);
        if (!containerIn.isEmpty() && state.tank.getFluidAmount() < state.tank.getCapacity()) {
            ItemStack containerOut = state.inventory.getStackInSlot(FILLED_FLUID_SLOT);
            ItemStack result = Utils.drainFluidContainer(state.tank, containerIn, containerOut);
            if (!result.isEmpty()) {
                if (!containerOut.isEmpty() && ItemHandlerHelper.canItemStacksStack(containerOut, result)) {
                    containerOut.grow(result.getCount());
                } else if (containerOut.isEmpty()) {
                    state.inventory.setStackInSlot(FILLED_FLUID_SLOT, result);
                }

                containerIn.shrink(1);
                if (containerIn.isEmpty()) {
                    state.inventory.setStackInSlot(EMPTY_FLUID_SLOT, ItemStack.EMPTY);
                }
                update = true;
            }
        }

        if (update) {
            ctx.markMasterDirty();
        }

        enqueueProcesses(state, level);
        handleItemOutput(ctx);
    }

    private void enqueueProcesses(State state, Level level) {
        if (state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize())
            return;

        if (state.processor.getQueueSize() > 0)
            return;

        ItemStack containerStack = state.inventory.getStackInSlot(INPUT_CONTAINER_SLOT);
        if (containerStack.isEmpty())
            return;

        int inputStart = 0;
        RangedWrapper inputOnly = new RangedWrapper(state.inventory, inputStart, NUM_INPUT_SLOTS);
        RecipeWrapper wrapper = new RecipeWrapper(inputOnly);

        Optional<FoodFermenterRecipe> recipeOpt = findRecipeWithFluid(wrapper, state.tank.getFluid(), level);

        if (recipeOpt.isPresent()) {
            FoodFermenterRecipe recipe = recipeOpt.get();

            if (!ItemStack.isSameItem(containerStack, recipe.container))
                return;

            int[][] slotData = resolveSlotsForRecipe(inputOnly, recipe, inputStart);
            if (slotData != null) {
                MultiblockProcessInMachine<FoodFermenterRecipe> process = new MultiblockProcessInMachine<>(
                        recipe, slotData[0]);
                process.setInputAmounts(slotData[1]);

                if (state.processor.addProcessToQueue(process, level, false)) {
                    state.tank.drain(recipe.fluidInput.copy(), IFluidHandler.FluidAction.EXECUTE);
                    containerStack.shrink(1);
                    if (containerStack.isEmpty()) {
                        state.inventory.setStackInSlot(INPUT_CONTAINER_SLOT, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private Optional<FoodFermenterRecipe> findRecipeWithFluid(RecipeWrapper wrapper, FluidStack fluid, Level level) {
        List<FoodFermenterRecipe> recipes = getAllProvidedRecipes(level);
        for (FoodFermenterRecipe recipe : recipes) {
            if (recipe.matches(wrapper, level)) {
                if (fluid.getAmount() >= recipe.fluidInput.getAmount() && fluid.isFluidEqual(recipe.fluidInput)) {
                    return Optional.of(recipe);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns int[2][] where [0] = slot indices, [1] = amounts per slot.
     * Aggregates all ingredients of the same type, then distributes evenly across
     * all matching slots.
     */
    private int[][] resolveSlotsForRecipe(IItemHandler handler, FoodFermenterRecipe recipe, int offset) {
        List<ItemStack> simulatedInv = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            simulatedInv.add(handler.getStackInSlot(i).copy());
        }

        List<int[]> aggregated = new ArrayList<>();
        boolean[] merged = new boolean[recipe.inputs.size()];

        for (int i = 0; i < recipe.inputs.size(); i++) {
            if (merged[i])
                continue;
            int total = recipe.inputs.get(i).getCount();
            for (int j = i + 1; j < recipe.inputs.size(); j++) {
                if (merged[j])
                    continue;
                if (ingredientsMatch(recipe.inputs.get(i), recipe.inputs.get(j), simulatedInv)) {
                    total += recipe.inputs.get(j).getCount();
                    merged[j] = true;
                }
            }
            aggregated.add(new int[]{i, total});
        }

        LinkedHashMap<Integer, Integer> slotAmounts = new LinkedHashMap<>();

        for (int[] entry : aggregated) {
            IngredientWithSize component = recipe.inputs.get(entry[0]);
            int remaining = entry[1];

            List<int[]> matchingSlots = new ArrayList<>();
            for (int i = 0; i < simulatedInv.size(); i++) {
                ItemStack stack = simulatedInv.get(i);
                if (!stack.isEmpty() && component.test(stack)) {
                    matchingSlots.add(new int[]{i, stack.getCount()});
                }
            }

            if (matchingSlots.isEmpty() || matchingSlots.stream().mapToInt(s -> s[1]).sum() < remaining)
                return null;

            while (remaining > 0 && !matchingSlots.isEmpty()) {
                int perSlot = Math.max(1, remaining / matchingSlots.size());
                Iterator<int[]> it = matchingSlots.iterator();
                while (it.hasNext() && remaining > 0) {
                    int[] slotInfo = it.next();
                    int slotIdx = slotInfo[0];
                    int available = slotInfo[1];
                    int take = Math.min(Math.min(perSlot, available), remaining);
                    if (take > 0) {
                        simulatedInv.get(slotIdx).shrink(take);
                        slotInfo[1] -= take;
                        remaining -= take;
                        slotAmounts.merge(offset + slotIdx, take, Integer::sum);
                    }
                    if (slotInfo[1] <= 0)
                        it.remove();
                }
            }

            if (remaining > 0)
                return null;
        }

        int[] slots = slotAmounts.keySet().stream().mapToInt(Integer::intValue).toArray();
        int[] amounts = slotAmounts.values().stream().mapToInt(Integer::intValue).toArray();
        return new int[][]{slots, amounts};
    }

    private boolean ingredientsMatch(IngredientWithSize a, IngredientWithSize b, List<ItemStack> inv) {
        for (ItemStack stack : inv) {
            if (!stack.isEmpty() && a.test(stack) != b.test(stack)) {
                return false;
            }
        }
        return true;
    }

    private void handleItemOutput(IMultiblockContext<State> ctx) {
        final State state = ctx.getState();

        // Push finished items from OUTPUT_SLOT to external capability
        ItemStack stackToPush = state.inventory.getStackInSlot(OUTPUT_SLOT);
        if (!stackToPush.isEmpty()) {
            ItemStack stack = ItemHandlerHelper.copyStackWithSize(stackToPush, 1);
            ItemStack remaining = Utils.insertStackIntoInventory(state.itemOutput, stack, false);
            if (remaining.isEmpty()) {
                stackToPush.shrink(1);
                ctx.markMasterDirty();
            }
        }
    }

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        final State state = context.getState();
        if (state.active && context.getLevel().getRawLevel().random.nextInt(10) == 0) {
            BlockPos pos = context.getLevel().toAbsolute(ITEM_INPUT);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.5D;
            double z = pos.getZ() + 0.5D;
            context.getLevel().getRawLevel().playLocalSound(x, y, z, ICContent.Sounds.COOKPOT_ACTIVE.get(),
                    SoundSource.BLOCKS, 0.5F,
                    context.getLevel().getRawLevel().random.nextFloat() * 0.2F + 0.9F, false);
        }
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop) {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    public static class State
            implements ContainerData, IMultiblockState, ProcessContext.ProcessContextInMachine<FoodFermenterRecipe> {
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final FluidTank tank = new FluidTank(TANK_CAPACITY);
        private final SlotwiseItemHandler inventory;
        private final MultiblockProcessor.InMachineProcessor<FoodFermenterRecipe> processor;
        private final Supplier<Level> levelSupplier;
        private final CapabilityReference<IItemHandler> itemOutput;
        private final LazyOptional<FluidTank> fluidTankCap;
        private final LazyOptional<IEnergyStorage> energyCap;
        private final LazyOptional<IItemHandler> itemInputCap;
        private final LazyOptional<IItemHandler> itemOutputCap;
        public boolean active;

        public State(IInitialMultiblockContext<State> ctx) {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final FoodFermenterLogic logic = (FoodFermenterLogic) ICContent.Multiblock.FOOD_FERMENTER.logic();
            this.levelSupplier = ctx.levelSupplier();

            this.inventory = SlotwiseItemHandler.makeWithGroups(List.of(
                    // 0-5: Inputs
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.NO_CONSTRAINT,
                            NUM_INPUT_SLOTS),
                    // 6: Empty Fluid Slot (Input Filled Bucket)
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.FLUID_INPUT, 1),
                    // 7: Filled Fluid Slot (Output Empty Bucket)
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1),
                    // 8: Container Input
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.NO_CONSTRAINT, 1),
                    // 9: Output
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1)), markDirty);

            this.processor = new MultiblockProcessor.InMachineProcessor<>(NUM_INPUT_SLOTS, 1.0F, 1, markDirty,
                    (level, id) -> logic.byKey(id, level));

            this.itemOutput = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, ITEM_OUTPUT);
            this.fluidTankCap = LazyOptional.of(() -> tank);
            this.energyCap = LazyOptional.of(() -> energy);

            this.itemInputCap = LazyOptional.of(() -> new WrappingItemHandler(inventory, true, false,
                    List.of(
                            new WrappingItemHandler.IntRange(0, NUM_INPUT_SLOTS), // Inputs
                            new WrappingItemHandler.IntRange(EMPTY_FLUID_SLOT, EMPTY_FLUID_SLOT + 1), // Fluid In
                            new WrappingItemHandler.IntRange(INPUT_CONTAINER_SLOT, INPUT_CONTAINER_SLOT + 1) // Container
                    )));

            this.itemOutputCap = LazyOptional.of(() -> new WrappingItemHandler(inventory, false, true,
                    List.of(
                            new WrappingItemHandler.IntRange(OUTPUT_SLOT, OUTPUT_SLOT + 1), // Output
                            new WrappingItemHandler.IntRange(FILLED_FLUID_SLOT, FILLED_FLUID_SLOT + 1) // Fluid Out
                    )));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt) {
            nbt.putBoolean("active", active);
            nbt.put("energy", energy.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("processor", processor.toNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt) {
            active = nbt.getBoolean("active");
            energy.deserializeNBT(nbt.get("energy"));
            tank.readFromNBT(nbt.getCompound("tank"));
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt) {
            nbt.putBoolean("active", active);
            nbt.put("energy", energy.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("processor", processor.toNBT());
        }

        @Override
        public void readSyncNBT(CompoundTag nbt) {
            active = nbt.getBoolean("active");
            energy.deserializeNBT(nbt.get("energy"));
            tank.readFromNBT(nbt.getCompound("tank"));
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
        }

        @Override
        public AveragingEnergyStorage getEnergy() {
            return energy;
        }

        @Override
        public IItemHandlerModifiable getInventory() {
            return inventory;
        }

        @Override
        public int[] getOutputTanks() {
            return new int[0];
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{OUTPUT_SLOT};
        }

        public MultiblockProcess<FoodFermenterRecipe, ProcessContextInMachine<FoodFermenterRecipe>> getProcess() {
            if (levelSupplier.get() != null) {
                List<MultiblockProcess<FoodFermenterRecipe, ProcessContextInMachine<FoodFermenterRecipe>>> queue = processor
                        .getQueue();
                if (!queue.isEmpty()) {
                    return queue.get(0);
                }
            }
            return null;
        }

        public int getProcessTick() {
            var activeProcess = getProcess();
            return activeProcess != null ? activeProcess.processTick : 0;
        }

        public int getMaxProcessTick() {
            var activeProcess = getProcess();
            return activeProcess != null ? activeProcess.getMaxTicks(levelSupplier.get()) : 0;
        }

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> getProcessTick();
                case 1 -> getMaxProcessTick();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }

        public FluidTank getTank() {
            return tank;
        }
    }
}
