package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.shapes.MixerShapes;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;
import uk.akkiserver.immersivecooking.common.utils.FluidUtils;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FoodProcessorLogic implements IMultiblockLogic<FoodProcessorLogic.State>, IServerTickableComponent<FoodProcessorLogic.State>, IClientTickableComponent<FoodProcessorLogic.State> {
    public static final BlockPos REDSTONE_POS = new BlockPos(2, 1, 2);
    public static final MultiblockFace ITEM_OUTPUT = new MultiblockFace(3, 0, 1, RelativeBlockFace.RIGHT);
    public static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(ITEM_OUTPUT);
    public static final CapabilityPosition FLUID_INPUT = new CapabilityPosition(0, 1, 1, RelativeBlockFace.RIGHT);
    public static final BlockPos ITEM_INPUT = new BlockPos(0, 1, 0);
    public static final CapabilityPosition ENERGY_POS = new CapabilityPosition(0, 1, 2, RelativeBlockFace.UP);

    public static final int TANK_CAPACITY = 12000;
    public static final int ENERGY_CAPACITY = 16000;

    public static final int NUM_INPUT_SLOTS = 8;
    public static final int EMPTY_FLUID_SLOT = 8;
    public static final int FILLED_FLUID_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int NUM_SLOTS = 11;

    @Override
    public FoodProcessorLogic.State createInitialState(IInitialMultiblockContext<FoodProcessorLogic.State> ctx) {
        return new FoodProcessorLogic.State(ctx);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return MixerShapes.SHAPE_GETTER;
    }

    @Override
    public void registerCapabilities(CapabilityRegistrar<FoodProcessorLogic.State> register) {
        register.registerAtOrNull(Capabilities.EnergyStorage.BLOCK, ENERGY_POS, state -> state.energy);
        register.register(Capabilities.ItemHandler.BLOCK, (state, pos) -> {
            if (ITEM_INPUT.equals(pos.posInMultiblock()))
                return state.itemInputCap;
            if (ITEM_OUTPUT_CAP.equals(pos))
                return state.itemOutputCap;
            return null;
        });
        register.registerAtOrNull(Capabilities.FluidHandler.BLOCK, FLUID_INPUT, state -> state.fluidInput);
    }

    @Override
    public void tickServer(IMultiblockContext<FoodProcessorLogic.State> ctx) {
        final FoodProcessorLogic.State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();

        boolean active = state.processor.tickServer(state, ctx.getLevel(), state.rsState.isEnabled(ctx));
        if (active != state.active) {
            state.active = active;
            ctx.requestMasterBESync();
        }

        if (FluidUtils.drainFluidContainer(state.tank, FILLED_FLUID_SLOT, EMPTY_FLUID_SLOT, state.inventory))
            ctx.markMasterDirty();

        enqueueProcesses(state, level);
        handleItemOutput(ctx);
    }

    private void enqueueProcesses(FoodProcessorLogic.State state, Level level) {
        if (state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize()) return;

        if (state.processor.getQueueSize() > 0) return;

        int inputStart = 0;
        RangedWrapper inputOnly = new RangedWrapper(state.inventory, inputStart, NUM_INPUT_SLOTS);
        RecipeWrapper wrapper = new RecipeWrapper(inputOnly);

        Optional<RecipeHolder<FoodProcessorRecipe>> recipeOpt = FoodProcessorRecipe.findRecipe(wrapper, state.tank.getFluid(), level);

        if (recipeOpt.isPresent()) {
            RecipeHolder<FoodProcessorRecipe> holder = recipeOpt.get();
            FoodProcessorRecipe recipe = holder.value();

            int[][] slotData = resolveSlotsForRecipe(inputOnly, recipe, inputStart);
            if (slotData != null) {
                MultiblockProcessInMachine<FoodProcessorRecipe> process = new MultiblockProcessInMachine<>(holder,
                        slotData[0]);
                process.setInputAmounts(new int[slotData[1].length]);

                if (state.processor.addProcessToQueue(process, level, false)) {
                    // Consume items immediately at start
                    for (int i = 0; i < slotData[0].length; i++) {
                        int slot = slotData[0][i];
                        int amount = slotData[1][i];
                        if (amount > 0) {
                            inputOnly.getStackInSlot(slot).shrink(amount);
                        }
                    }

                    if (recipe.fluidInput != null) {
                        FluidStack toDrain = state.tank.getFluid().copyWithAmount(recipe.fluidInput.amount());
                        state.tank.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }
    }

    /**
     * Returns int[2][] where [0] = slot indices, [1] = amounts per slot.
     * Aggregates all ingredients of the same type, then distributes evenly across
     * all matching slots.
     */
    private int[][] resolveSlotsForRecipe(IItemHandler handler, FoodProcessorRecipe recipe, int offset) {
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
            aggregated.add(new int[] { i, total });
        }

        LinkedHashMap<Integer, Integer> slotAmounts = new LinkedHashMap<>();

        for (int[] entry : aggregated) {
            IngredientWithSize component = recipe.inputs.get(entry[0]);
            int remaining = entry[1];

            List<int[]> matchingSlots = new ArrayList<>();
            for (int i = 0; i < simulatedInv.size(); i++) {
                ItemStack stack = simulatedInv.get(i);
                if (!stack.isEmpty() && component.test(stack)) {
                    matchingSlots.add(new int[] { i, stack.getCount() });
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
        return new int[][] { slots, amounts };
    }

    private boolean ingredientsMatch(IngredientWithSize a, IngredientWithSize b, List<ItemStack> inv) {
        for (ItemStack stack : inv) {
            if (!stack.isEmpty() && a.test(stack) != b.test(stack)) {
                return false;
            }
        }
        return true;
    }

    private void handleItemOutput(IMultiblockContext<FoodProcessorLogic.State> ctx) {
        final FoodProcessorLogic.State state = ctx.getState();

        ItemStack stackToPush = state.inventory.getStackInSlot(OUTPUT_SLOT);
        if (!stackToPush.isEmpty()) {
            ItemStack stack = stackToPush.copyWithCount(1);
            ItemStack remaining = Utils.insertStackIntoInventory((Supplier<IItemHandler>) () -> state.itemOutput, stack, false);
            if (remaining.isEmpty()) {
                stackToPush.shrink(1);
                ctx.markMasterDirty();
            }
        }
    }

    @Override
    public void tickClient(IMultiblockContext<FoodProcessorLogic.State> context) {
        final FoodProcessorLogic.State state = context.getState();
        if (!state.isPlayingSound.getAsBoolean()) {
            final Vec3 soundPos = context.getLevel().toAbsolute(new Vec3(0.5, 1.5, 0.5));

            state.isPlayingSound = MultiblockSound.startSound(
                    () -> state.active,
                    context.isValid(),
                    soundPos,
                    ICContent.Sounds.FOOD_FERMENTER_ACTIVE,
                    0.5f);
        }
    }

    @Override
    public void dropExtraItems(FoodProcessorLogic.State state, Consumer<ItemStack> drop) {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    public static class State
            implements ContainerData, IMultiblockState, ProcessContext.ProcessContextInMachine<FoodProcessorRecipe> {
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final FluidTank tank = new FluidTank(TANK_CAPACITY);
        private final SlotwiseItemHandler inventory;
        private final MultiblockProcessor.InMachineProcessor<FoodProcessorRecipe> processor;
        private final Supplier<Level> levelSupplier;
        private final IItemHandler itemOutput;
        private final IFluidHandler fluidInput;
        private final IItemHandler itemInputCap;
        private final IItemHandler itemOutputCap;
        private int catalystType;
        private BooleanSupplier isPlayingSound = () -> false;
        public boolean active;

        public State(IInitialMultiblockContext<FoodProcessorLogic.State> ctx) {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            this.levelSupplier = ctx.levelSupplier();

            this.inventory = SlotwiseItemHandler.makeWithGroups(List.of(
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.NO_CONSTRAINT, NUM_INPUT_SLOTS),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1),
                    new SlotwiseItemHandler.IOConstraintGroup(new SlotwiseItemHandler.IOConstraint(true, Utils::isFluidRelatedItemStack), 1),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1)), markDirty);

            this.processor = new MultiblockProcessor.InMachineProcessor<>(NUM_INPUT_SLOTS, 1.0F, 1, markDirty,
                    (level, id) -> FoodProcessorRecipe.RECIPES.get(id).value());

            this.itemOutput = ctx.getCapabilityAt(Capabilities.ItemHandler.BLOCK, ITEM_OUTPUT).get();
            this.fluidInput = ArrayFluidHandler.fillOnly(tank, markDirty);

            this.itemInputCap = new WrappingItemHandler(inventory, true, false,
                    List.of(
                            new WrappingItemHandler.IntRange(0, NUM_INPUT_SLOTS),
                            new WrappingItemHandler.IntRange(EMPTY_FLUID_SLOT, EMPTY_FLUID_SLOT + 1)
                    )
            );

            this.itemOutputCap = new WrappingItemHandler(inventory, false, true,
                    List.of(
                            new WrappingItemHandler.IntRange(OUTPUT_SLOT, OUTPUT_SLOT + 1),
                            new WrappingItemHandler.IntRange(FILLED_FLUID_SLOT, FILLED_FLUID_SLOT + 1)
                    )
            );
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.putBoolean("active", active);
            nbt.put("energy", energy.serializeNBT(provider));
            nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("inventory", inventory.serializeNBT(provider));
            nbt.put("processor", processor.toNBT(provider));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            active = nbt.getBoolean("active");
            energy.deserializeNBT(provider, nbt.get("energy"));
            tank.readFromNBT(provider, nbt.getCompound("tank"));
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            processor.fromNBT(
                    nbt.get("processor"),
                    (getRecipe, data, p) -> new MultiblockProcessInMachine<>(getRecipe, data),
                    provider
            );
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            writeSaveNBT(nbt, provider);
            nbt.putInt("catalyst", catalystType);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            readSaveNBT(nbt, provider);
            catalystType = nbt.getInt("catalyst");
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
            return new int[] { OUTPUT_SLOT };
        }

        public MultiblockProcess<FoodProcessorRecipe, ProcessContextInMachine<FoodProcessorRecipe>> getProcess() {
            if (levelSupplier.get() != null) {
                List<MultiblockProcess<FoodProcessorRecipe, ProcessContextInMachine<FoodProcessorRecipe>>> queue = processor.getQueue();
                if (!queue.isEmpty()) {
                    return queue.getFirst();
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

        public int getCatalyst() {
            return catalystType;
        }
    }
}