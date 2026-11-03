package uk.akkiserver.immersivecooking.common.block.multiblock.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.inventory.InsertOnlyInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import uk.akkiserver.immersivecooking.common.block.multiblock.shape.SlicerShape;
import uk.akkiserver.immersivecooking.common.block.multiblock.process.SlicerProcess;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;

import java.util.function.Function;
import java.util.function.Consumer;

public class SlicerLogic implements IMultiblockLogic<SlicerLogic.State>, IServerTickableComponent<SlicerLogic.State>, IClientTickableComponent<SlicerLogic.State> {
    public static final BlockPos REDSTONE_POS = new BlockPos(1, 0, 1);
    public static final CapabilityPosition INPUT_POS = new CapabilityPosition(1, 1, 2, RelativeBlockFace.BACK);
    public static final CapabilityPosition ENERGY_POS = new CapabilityPosition(0, 1, 2, RelativeBlockFace.UP);
    public static final MultiblockFace OUTPUT_POS = new MultiblockFace(1, 1, -1, RelativeBlockFace.BACK);
    public static final int ENERGY_CAPACITY = 16000;

    @Override
    public void tickClient(IMultiblockContext<State> context) {
        State state = context.getState();
        if (state.active) {
            var level = context.getLevel().getRawLevel();
            for (var process : state.processor.getQueue()) {
                process.processTick = Math.min(process.processTick + 1, process.getMaxTicks(level));
            }
        }
    }

    @Override
    public void tickServer(IMultiblockContext<State> context) {
        State state = context.getState();
        boolean prepared = false;
        for (var process : state.processor.getQueue())
            if (process instanceof SlicerProcess slicerProcess)
                prepared |= slicerProcess.prepareOutputs(context.getLevel().getRawLevel());
        if (prepared) {
            context.markMasterDirty();
            context.requestMasterBESync();
        }
        boolean active = state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if (active != state.active) {
            state.active = active;
            context.markMasterDirty();
            context.requestMasterBESync();
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capabilitySource) {
        return new State(capabilitySource);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return SlicerShape.SHAPE_GETTER;
    }

    @Override
    public void registerCapabilities(CapabilityRegistrar<State> register) {
        register.registerAtOrNull(Capabilities.ItemHandler.BLOCK, INPUT_POS, state -> state.input);
        register.registerAtOrNull(Capabilities.EnergyStorage.BLOCK, ENERGY_POS, state -> state.energy);
    }

    @Override
    public void onEntityCollision(IMultiblockContext<State> context, BlockPos pos, Entity entity) {
        if (context.getLevel().getRawLevel().isClientSide || !pos.equals(INPUT_POS.posInMultiblock())) return;
        if (entity instanceof ItemEntity item && item.isAlive()) {
            ItemStack remainder = context.getState().input.insertItem(0, item.getItem(), false);
            item.setItem(remainder);
            if (remainder.isEmpty()) item.discard();
        }
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop) {
        // Queued inputs have already left the inserting inventory or conveyor.
        for (var process : state.processor.getQueue()) {
            if (process instanceof MultiblockProcessInWorld<SlicerRecipe> inWorld) {
                for (ItemStack stack : inWorld.inputItems) {
                    if (!stack.isEmpty()) {
                        drop.accept(stack.copy());
                    }
                }
            }
        }

        state.processor.clear();
    }

    public static class State implements IMultiblockState, ProcessContextInWorld<SlicerRecipe> {
        public final AveragingEnergyStorage energy;
        public final RSState rsState = RSState.enabledByDefault();
        public final MultiblockProcessor<SlicerRecipe, ProcessContextInWorld<SlicerRecipe>> processor;
        public final IItemHandler input;
        public boolean active;
        private final DroppingMultiblockOutput output;

        public State(IInitialMultiblockContext<State> context) {
            energy = new AveragingEnergyStorage(ENERGY_CAPACITY) {
                @Override
                public int receiveEnergy(int amount, boolean simulate) {
                    int received = super.receiveEnergy(amount, simulate);
                    if (!simulate && received > 0) context.getMarkDirtyRunnable().run();
                    return received;
                }
            };
            processor = new MultiblockProcessor<>(3,
                    ticks -> .34,
                    3,
                    context.getMarkDirtyRunnable(),
                    context.getSyncRunnable(),
                    (level, id) -> {
                        RecipeHolder<SlicerRecipe> holder = SlicerRecipe.RECIPES.get(id);
                        return holder == null ? null : holder.value();
                    }
            );
            input = new InsertOnlyInventory() {
                @Override
                protected ItemStack insert(ItemStack stack, boolean simulate) {
                    var recipe = SlicerRecipe.findRecipe(stack).orElse(null);
                    if (recipe == null) return stack;
                    int amount = recipe.value().input.getCount();
                    var process = new SlicerProcess(recipe, stack.copyWithCount(amount));
                    if (!processor.addProcessToQueue(process, context.levelSupplier().get(), simulate)) return stack;
                    return stack.copyWithCount(stack.getCount() - amount);
                }
            };
            output = new DroppingMultiblockOutput(OUTPUT_POS, context);
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.put("energy", energy.serializeNBT(provider));
            writeSyncNBT(nbt, provider);
        }

        @Override
        public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            if (nbt.contains("energy")) energy.deserializeNBT(provider, nbt.get("energy"));
            readSyncNBT(nbt, provider);
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.putBoolean("active", active);
            nbt.put("processor", processor.toNBT(provider));
        }

        @Override
        public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            active = nbt.getBoolean("active");
            processor.fromNBT(nbt.get("processor"), SlicerProcess::new, provider);
        }

        @Override
        public AveragingEnergyStorage getEnergy() {
            return energy;
        }

        @Override
        public void doProcessOutput(ItemStack result, IMultiblockLevel level) {
            output.insertOrDrop(result.copy(), level);
        }
    }
}
