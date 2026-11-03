package uk.akkiserver.immersivecooking.common.block.multiblock.process;

import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;

import java.util.List;
import java.util.function.BiFunction;

/** Keeps a batch's rolled outputs identical for rendering, saving and final output. */
public class SlicerProcess extends MultiblockProcessInWorld<SlicerRecipe> {
    private NonNullList<ItemStack> outputs;

    public SlicerProcess(RecipeHolder<SlicerRecipe> recipe, ItemStack input) {
        super(recipe, input);
    }

    public SlicerProcess(BiFunction<Level, ResourceLocation, SlicerRecipe> getRecipe,
                         CompoundTag tag, HolderLookup.Provider provider) {
        super(getRecipe, tag, provider);
        if (tag.contains("slicerOutputs")) {
            CompoundTag outputTag = tag.getCompound("slicerOutputs");
            outputs = NonNullList.withSize(outputTag.getInt("size"), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(outputTag, outputs, provider);
        }
    }

    public boolean prepareOutputs(Level level) {
        if (outputs != null) return false;
        var recipe = getLevelData(level).recipe();
        outputs = recipe == null ? NonNullList.create() : recipe.getActualItemOutputs();
        return true;
    }

    @Override
    protected List<ItemStack> getRecipeItemOutputs(Level level, ProcessContextInWorld<SlicerRecipe> context) {
        prepareOutputs(level);
        return outputs;
    }

    @Override
    public List<ItemStack> getDisplayItem(Level level) {
        if (outputs != null && processTick / (float) getMaxTicks(level) > transformationPoint)
            return outputs;
        return inputItems;
    }

    @Override
    public void writeExtraDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.writeExtraDataToNBT(tag, provider);
        if (outputs != null) {
            CompoundTag outputTag = new CompoundTag();
            outputTag.putInt("size", outputs.size());
            ContainerHelper.saveAllItems(outputTag, outputs, provider);
            tag.put("slicerOutputs", outputTag);
        }
    }
}
