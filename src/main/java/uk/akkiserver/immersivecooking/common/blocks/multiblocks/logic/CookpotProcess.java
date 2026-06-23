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
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

import java.util.function.BiFunction;

public class CookpotProcess extends MultiblockProcessInMachine<CookpotRecipe> {
    public CookpotProcess(RecipeHolder<CookpotRecipe> recipe, int[] inputSlots) {
        super(recipe, inputSlots);
    }

    public CookpotProcess(BiFunction<Level, ResourceLocation, CookpotRecipe> recipe, CompoundTag data, HolderLookup.Provider provider) {
        super(recipe, data);
    }

    @Override
    protected void outputItem(ProcessContext.ProcessContextInMachine<CookpotRecipe> ctx, ItemStack output, IMultiblockLevel mbLevel) {
        if (output == null || output.isEmpty())
            return;

        IItemHandlerModifiable inventory = ctx.getInventory();
        ItemStack current = inventory.getStackInSlot(CookpotLogic.OUTPUT_RAW_SLOT);

        if (current.isEmpty()) {
            inventory.setStackInSlot(CookpotLogic.OUTPUT_RAW_SLOT, output.copy());
        } else if (ItemStack.isSameItemSameComponents(current, output)) {
            current.grow(output.getCount());
        }
    }

    @Override
    protected boolean canOutputItem(ProcessContext.ProcessContextInMachine<CookpotRecipe> context, ItemStack output) {
        ItemStack current = context.getInventory().getStackInSlot(CookpotLogic.OUTPUT_RAW_SLOT);
        if (current.isEmpty())
            return true;

        return ItemStack.isSameItemSameComponents(current, output)
                && current.getCount() + output.getCount() <= current.getMaxStackSize();
    }
}