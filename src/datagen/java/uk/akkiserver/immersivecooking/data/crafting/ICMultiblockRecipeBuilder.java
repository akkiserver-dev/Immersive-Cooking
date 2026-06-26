package uk.akkiserver.immersivecooking.data.crafting;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class ICMultiblockRecipeBuilder<B extends ICMultiblockRecipeBuilder<B, R>, R extends MultiblockRecipe> extends ICGenericBuilder<B, R> {

    protected int energy;
    protected int time;

    /** Accumulated item inputs, used by recipes with multiple input slots (Cookpot, Food Fermenter, Food Processor) */
    protected final List<IngredientWithSize> inputs = new ArrayList<>();

    @Override
    protected abstract R makeInstance();

    public B setTimeAndEnergy(int time, int energy) {
        this.energy = energy;
        this.time = time;

        validateTimeAndEnergy();

        return (B) this;
    }

    protected void validateTimeAndEnergy() {
        if (this.energy <= 0)
            throw new IllegalStateException("Energy consumption must be between 1 - " + Integer.MAX_VALUE);

        if (this.time <= 0)
            throw new IllegalStateException("Time must be between 1 - " + Integer.MAX_VALUE);
    }

    // ########################################################################################################

    public B addInput(IngredientWithSize ingredient) {
        this.inputs.add(ingredient);
        return (B) this;
    }

    public B addInput(ItemLike itemLike, int amount) {
        return addInput(itemIngredient(itemLike, amount));
    }

    public B addInput(TagKey<Item> tag, int amount) {
        return addInput(itemIngredient(tag, amount));
    }

    protected NonNullList<IngredientWithSize> getInputs() {
        NonNullList<IngredientWithSize> list = NonNullList.create();
        list.addAll(this.inputs);
        return list;
    }

    // ########################################################################################################

    protected static IngredientWithSize itemIngredient(ItemStack stack) {
        return itemIngredient(Ingredient.of(stack.getItem()), stack.getCount());
    }

    protected static IngredientWithSize itemIngredient(ItemLike itemLike, int amount) {
        return itemIngredient(Ingredient.of(itemLike), amount);
    }

    protected static IngredientWithSize itemIngredient(Ingredient ingredient, int amount) {
        return new IngredientWithSize(ingredient, amount);
    }

    protected static IngredientWithSize itemIngredient(TagKey<Item> tag, int amount) {
        return new IngredientWithSize(tag, amount);
    }

    protected static SizedFluidIngredient fluidIngredient(FluidStack fluidStack) {
        return fluidIngredient(fluidStack.getFluid(), fluidStack.getAmount());
    }

    protected static SizedFluidIngredient fluidIngredient(Fluid fluid, int amount) {
        return new SizedFluidIngredient(FluidIngredient.single(fluid), amount);
    }

    protected static SizedFluidIngredient fluidIngredient(TagKey<Fluid> tag, int amount) {
        return new SizedFluidIngredient(FluidIngredient.tag(tag), amount);
    }
}