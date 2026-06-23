package uk.akkiserver.immersivecooking.data.recipes;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FoodFermenterRecipeBuilder extends ICMultiblockRecipeBuilder<FoodFermenterRecipeBuilder, FoodFermenterRecipe> {

    public static FoodFermenterRecipeBuilder builder(@Nonnull ItemStack result) {
        return new FoodFermenterRecipeBuilder(result);
    }

    public static FoodFermenterRecipeBuilder builder(@Nonnull ItemLike result) {
        return new FoodFermenterRecipeBuilder(new ItemStack(result));
    }

    private final ItemStack result;
    private ItemStack container = ItemStack.EMPTY;
    private SizedFluidIngredient fluidInput;

    private FoodFermenterRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    @Override
    protected FoodFermenterRecipe makeInstance() {
        Objects.requireNonNull(this.result, "FoodFermenterRecipe must have a result item.");
        if (this.inputs.isEmpty())
            throw new IllegalStateException("FoodFermenterRecipe must have at least one input.");

        validateTimeAndEnergy();

        return new FoodFermenterRecipe(getInputs(), this.fluidInput, this.result, this.container, this.time, this.energy);
    }

    public FoodFermenterRecipeBuilder setFluidInput(FluidStack fluid) {
        this.fluidInput = fluidIngredient(fluid);
        return this;
    }

    public FoodFermenterRecipeBuilder setFluidInput(Fluid fluid, int amount) {
        this.fluidInput = fluidIngredient(fluid, amount);
        return this;
    }

    public FoodFermenterRecipeBuilder setFluidInput(TagKey<Fluid> tag, int amount) {
        this.fluidInput = fluidIngredient(tag, amount);
        return this;
    }

    public FoodFermenterRecipeBuilder setContainer(ItemLike container) {
        return setContainer(new ItemStack(container));
    }

    public FoodFermenterRecipeBuilder setContainer(ItemStack container) {
        this.container = container;
        return this;
    }
}