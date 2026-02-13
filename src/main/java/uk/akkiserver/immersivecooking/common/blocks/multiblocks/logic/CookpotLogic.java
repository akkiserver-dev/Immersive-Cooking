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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.CookpotLogic.State;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.shapes.CookpotShape;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.providers.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CookpotLogic extends ICMultiblockLogic<State, CookpotRecipe>
        implements IServerTickableComponent<State>, IClientTickableComponent<State> {
    public static final BlockPos REDSTONE_POS = new BlockPos(2, 1, 2);
    public static final MultiblockFace ITEM_OUTPUT = new MultiblockFace(3, 0, 1, RelativeBlockFace.RIGHT);
    public static final CapabilityPosition ITEM_OUTPUT_CAP = CapabilityPosition.opposing(ITEM_OUTPUT);
    public static final BlockPos ITEM_INPUT = new BlockPos(0, 1, 0);
    public static final CapabilityPosition ENERGY_POS = new CapabilityPosition(0, 1, 2, RelativeBlockFace.UP);

    public static final int NUM_INPUT_SLOTS = 6;
    public static final int BOWL_SLOT = 6;
    public static final int OUTPUT_RAW_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int NUM_SLOTS = 9;
    public static final int ENERGY_CAPACITY = 16000;

    public CookpotLogic() {
        /* Default Registry */
        this.recipeProviders.add(new DefaultCookpotRecipeProvider());
        /* Farmer's Delight */
        this.recipeProviders.add(new FDCookpotRecipeProvider());
        /* Farm & Charm */
        this.recipeProviders.add(new FCCookpotRecipeProvider());
        this.recipeProviders.add(new FCCookpotRoasterRecipeProvider());
        this.recipeProviders.add(new FCCookpotStoveRecipeProvider());
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> ctx) {
        return new State(ctx);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return CookpotShape.SHAPE_GETTER;
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
    public void tickServer(IMultiblockContext<State> context) {
        final State state = context.getState();
        final Level level = context.getLevel().getRawLevel();

        boolean active = state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if (active != state.active) {
            state.active = active;
            context.requestMasterBESync();
        }

        enqueueProcesses(state, level);
        handleItemOutput(context);
    }

    private void enqueueProcesses(State state, Level level) {
        if (state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize())
            return;

        if (state.processor.getQueueSize() > 0)
            return;

        int inputStart = 0;

        RangedWrapper inputOnly = new RangedWrapper(state.inventory, inputStart, NUM_INPUT_SLOTS);
        RecipeWrapper wrapper = new RecipeWrapper(inputOnly);

        Optional<CookpotRecipe> recipeOpt = this.findRecipe(wrapper, level);

        if (recipeOpt.isPresent()) {
            CookpotRecipe recipe = recipeOpt.get();
            int[][] slotData = resolveSlotsForRecipe(inputOnly, recipe, inputStart);
            if (slotData != null) {
                MultiblockProcessInMachine<CookpotRecipe> process = new MultiblockProcessInMachine<>(recipe,
                        slotData[0]);
                process.setInputAmounts(new int[slotData[1].length]);
                if (state.processor.addProcessToQueue(process, level, false)) {
                    for (int i = 0; i < slotData[0].length; i++) {
                        int slot = slotData[0][i];
                        int amount = slotData[1][i];
                        if (amount > 0) {
                            inputOnly.getStackInSlot(slot).shrink(amount);
                        }
                    }
                }
            }
        }
    }

    private int[][] resolveSlotsForRecipe(IItemHandler handler, CookpotRecipe recipe, int offset) {
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

    /**
     * Check if two IngredientWithSize match the same item types in the inventory
     */
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

        ItemStack stackToPush = state.inventory.getStackInSlot(OUTPUT_SLOT);
        if (!stackToPush.isEmpty()) {
            ItemStack stack = ItemHandlerHelper.copyStackWithSize(stackToPush, 1);
            ItemStack remaining = Utils.insertStackIntoInventory(state.itemOutput, stack, false);
            if (remaining.isEmpty()) {
                stackToPush.shrink(1);
                ctx.markMasterDirty();
            }
        }

        ItemStack rawOutput = state.inventory.getStackInSlot(OUTPUT_RAW_SLOT);
        if (rawOutput.isEmpty())
            return;

        Level level = ctx.getLevel().getRawLevel();

        ItemStack requiredContainer = getContainerForMeal(level, rawOutput);

        if (requiredContainer.isEmpty()) {
            ItemStack currentOutput = state.inventory.getStackInSlot(OUTPUT_SLOT);
            ItemStack rawCopy = rawOutput.copy();

            if (currentOutput.isEmpty()) {
                state.inventory.setStackInSlot(OUTPUT_SLOT, rawCopy);
                state.inventory.setStackInSlot(OUTPUT_RAW_SLOT, ItemStack.EMPTY);
                ctx.markMasterDirty();
            } else if (ItemStack.isSameItemSameTags(currentOutput, rawCopy)
                    && currentOutput.getCount() + rawCopy.getCount() <= currentOutput.getMaxStackSize()) {
                currentOutput.grow(rawCopy.getCount());
                state.inventory.setStackInSlot(OUTPUT_RAW_SLOT, ItemStack.EMPTY);
                ctx.markMasterDirty();
            }
            return;
        }

        ItemStack inputContainer = state.inventory.getStackInSlot(BOWL_SLOT);

        if (inputContainer.isEmpty() || !isContainerValid(inputContainer, requiredContainer)) {
            return;
        }

        ItemStack singleMealResult = rawOutput.copy();
        singleMealResult.setCount(1);

        ItemStack currentOutput = state.inventory.getStackInSlot(OUTPUT_SLOT);
        ItemStack currentRaw = state.inventory.getStackInSlot(OUTPUT_RAW_SLOT);
        ItemStack currentBowl = state.inventory.getStackInSlot(BOWL_SLOT);

        int rawCount = currentRaw.getCount();
        int bowlCount = currentBowl.getCount();
        int outputSpace = currentOutput.isEmpty()
                ? rawOutput.getMaxStackSize()
                : (ItemStack.isSameItemSameTags(currentOutput, singleMealResult)
                        ? currentOutput.getMaxStackSize() - currentOutput.getCount()
                        : 0);

        int moveCount = Math.min(Math.min(rawCount, bowlCount), outputSpace);
        if (moveCount <= 0)
            return;

        currentRaw.shrink(moveCount);
        if (currentRaw.isEmpty())
            state.inventory.setStackInSlot(OUTPUT_RAW_SLOT, ItemStack.EMPTY);

        currentBowl.shrink(moveCount);
        if (currentBowl.isEmpty())
            state.inventory.setStackInSlot(BOWL_SLOT, ItemStack.EMPTY);

        if (currentOutput.isEmpty()) {
            ItemStack result = singleMealResult.copy();
            result.setCount(moveCount);
            state.inventory.setStackInSlot(OUTPUT_SLOT, result);
        } else {
            currentOutput.grow(moveCount);
        }

        ctx.markMasterDirty();
    }

    private ItemStack getContainerForMeal(Level level, ItemStack meal) {
        Optional<CookpotRecipe> recipe = findRecipe(meal, level);
        if (recipe.isPresent()) {
            return recipe.get().container;
        } else {
            return ItemStack.EMPTY;
        }
    }

    private boolean isContainerValid(ItemStack input, ItemStack required) {
        return ItemStack.isSameItem(input, required);
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop) {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    public static class State
            implements IMultiblockState, ProcessContext.ProcessContextInMachine<CookpotRecipe>, ContainerData {
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final SlotwiseItemHandler inventory;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final MultiblockProcessor.InMachineProcessor<CookpotRecipe> processor;

        private final CapabilityReference<IItemHandler> itemOutput;
        private final LazyOptional<IEnergyStorage> energyCap;
        private final LazyOptional<IItemHandler> itemInputCap;
        private final LazyOptional<IItemHandler> itemOutputCap;

        public boolean active;
        private final Supplier<Level> levelSupplier;

        public State(IInitialMultiblockContext<State> ctx) {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final CookpotLogic logic = (CookpotLogic) ICContent.Multiblock.COOKPOT.logic();
            this.levelSupplier = ctx.levelSupplier();
            this.inventory = SlotwiseItemHandler.makeWithGroups(List.of(
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.NO_CONSTRAINT,
                            NUM_INPUT_SLOTS),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.NO_CONSTRAINT, 1),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.BLOCKED, 1),
                    new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1)),
                    markDirty);

            this.processor = new MultiblockProcessor.InMachineProcessor<>(NUM_INPUT_SLOTS, 1.0F, 1, markDirty,
                    (level, id) -> logic.byKey(id, level));

            this.itemOutput = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, ITEM_OUTPUT);

            this.energyCap = LazyOptional.of(() -> energy);
            this.itemInputCap = LazyOptional.of(() -> new WrappingItemHandler(inventory, true, false,
                    new WrappingItemHandler.IntRange(0, NUM_INPUT_SLOTS)));
            this.itemOutputCap = LazyOptional.of(() -> new WrappingItemHandler(inventory, false, true,
                    new WrappingItemHandler.IntRange(OUTPUT_SLOT, OUTPUT_SLOT + 1)));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt) {
            nbt.putBoolean("active", active);
            nbt.put("energy", energy.serializeNBT());
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("processor", processor.toNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt) {
            active = nbt.getBoolean("active");
            energy.deserializeNBT(nbt.get("energy"));
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            processor.fromNBT(nbt.get("processor"), MultiblockProcessInMachine::new);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt) {
            nbt.putBoolean("active", active);
            nbt.put("energy", energy.serializeNBT());
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("processor", processor.toNBT());
        }

        @Override
        public void readSyncNBT(CompoundTag nbt) {
            active = nbt.getBoolean("active");
            energy.deserializeNBT(nbt.get("energy"));
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
            return new int[] { OUTPUT_RAW_SLOT };
        }

        public MultiblockProcess<CookpotRecipe, ProcessContextInMachine<CookpotRecipe>> getProcess() {
            if (levelSupplier.get() != null) {
                List<MultiblockProcess<CookpotRecipe, ProcessContextInMachine<CookpotRecipe>>> queue = processor
                        .getQueue();

                if (!queue.isEmpty()) {
                    return queue.get(0);
                }
            }
            return null;
        }

        public int getProcessTick() {
            MultiblockProcess<CookpotRecipe, ProcessContextInMachine<CookpotRecipe>> activeProcess = getProcess();
            return activeProcess != null ? activeProcess.processTick : 0;
        }

        public int getMaxProcessTick() {
            MultiblockProcess<CookpotRecipe, ProcessContextInMachine<CookpotRecipe>> activeProcess = getProcess();
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
}
