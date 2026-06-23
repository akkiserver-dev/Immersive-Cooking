package uk.akkiserver.immersivecooking.data.recipes;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FoodProcessorRecipeBuilder extends ICMultiblockRecipeBuilder<FoodProcessorRecipeBuilder, FoodProcessorRecipe> {

    public static FoodProcessorRecipeBuilder builder(@Nonnull ItemStack result) {
        return new FoodProcessorRecipeBuilder(result);
    }

    public static FoodProcessorRecipeBuilder builder(@Nonnull ItemLike result) {
        return new FoodProcessorRecipeBuilder(new ItemStack(result));
    }

    private final ItemStack result;
    private SizedFluidIngredient fluidInput;

    private FoodProcessorRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    @Override
    protected FoodProcessorRecipe makeInstance() {
        Objects.requireNonNull(this.result, "FoodProcessorRecipe must have a result item.");
        if (this.inputs.isEmpty())
            throw new IllegalStateException("FoodProcessorRecipe must have at least one input.");

        validateTimeAndEnergy();

        return new FoodProcessorRecipe(getInputs(), this.fluidInput, this.result, this.time, this.energy);
    }

    public FoodProcessorRecipeBuilder setFluidInput(FluidStack fluid) {
        this.fluidInput = fluidIngredient(fluid);
        return this;
    }

    public FoodProcessorRecipeBuilder setFluidInput(Fluid fluid, int amount) {
        this.fluidInput = fluidIngredient(fluid, amount);
        return this;
    }

    public FoodProcessorRecipeBuilder setFluidInput(TagKey<Fluid> tag, int amount) {
        this.fluidInput = fluidIngredient(tag, amount);
        return this;
    }
}