package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;

import java.util.function.BiFunction;

public class FoodFermenterProcess extends MultiblockProcessInMachine<FoodFermenterRecipe> {
    public FoodFermenterProcess(RecipeHolder<FoodFermenterRecipe> recipe, int[] inputSlots) {
        super(recipe, inputSlots);
    }

    public FoodFermenterProcess(BiFunction<Level, ResourceLocation, FoodFermenterRecipe> recipe, CompoundTag data, HolderLookup.Provider provider) {
        super(recipe, data);
    }

    @Override
    protected void outputItem(ProcessContext.ProcessContextInMachine<FoodFermenterRecipe> ctx, ItemStack output, IMultiblockLevel mbLevel) {
        if (output == null || output.isEmpty())
            return;

        IItemHandlerModifiable inventory = ctx.getInventory();
        ItemStack current = inventory.getStackInSlot(FoodFermenterLogic.OUTPUT_SLOT);

        if (current.isEmpty()) {
            inventory.setStackInSlot(FoodFermenterLogic.OUTPUT_SLOT, output.copy());
        } else if (ItemStack.isSameItemSameComponents(current, output)) {
            current.grow(output.getCount());
        }
    }

    @Override
    protected boolean canOutputItem(ProcessContext.ProcessContextInMachine<FoodFermenterRecipe> context, ItemStack output) {
        ItemStack current = context.getInventory().getStackInSlot(FoodFermenterLogic.OUTPUT_SLOT);
        if (current.isEmpty())
            return true;

        return ItemStack.isSameItemSameComponents(current, output)
                && current.getCount() + output.getCount() <= current.getMaxStackSize();
    }
}