package uk.akkiserver.immersivecooking.data.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;

import java.util.ArrayList;
import java.util.List;

public class SlicerRecipeBuilder extends ICMultiblockRecipeBuilder<SlicerRecipeBuilder, SlicerRecipe> {
    private final ItemStack result;
    private final List<StackWithChance> byproducts = new ArrayList<>();

    private SlicerRecipeBuilder(ItemStack result) {
        this.result = result.copy();
    }

    public static SlicerRecipeBuilder builder(ItemStack result) {
        return new SlicerRecipeBuilder(result);
    }

    public static SlicerRecipeBuilder builder(ItemLike result) {
        return builder(new ItemStack(result));
    }

    public SlicerRecipeBuilder addByproduct(ItemStack stack) {
        return addByproduct(stack, 1);
    }

    public SlicerRecipeBuilder addByproduct(ItemLike item, int count) {
        return addByproduct(new ItemStack(item, count));
    }

    public SlicerRecipeBuilder addByproduct(ItemStack stack, float chance) {
        if (stack.isEmpty() || !Float.isFinite(chance) || chance < 0 || chance > 1)
            throw new IllegalArgumentException("Byproducts require a non-empty stack and a chance between 0 and 1");
        byproducts.add(new StackWithChance(stack.copy(), chance));
        return this;
    }

    @Override
    protected SlicerRecipe makeInstance() {
        if (inputs.size() != 1 || result.isEmpty())
            throw new IllegalStateException("Slicer recipes require one input and a non-empty output");
        validateTimeAndEnergy();
        return new SlicerRecipe(inputs.getFirst(), result, byproducts, time, energy);
    }
}
