package uk.akkiserver.immersivecooking.common.crafting.providers.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultFoodFermenterRecipeProvider implements IFluidContainingMultiblockRecipeProvider<FoodFermenterRecipe> {
    private FoodFermenterRecipe lastRecipe;

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(Container container, Level level) {
        if (lastRecipe != null && lastRecipe.matches(container, level) && lastRecipe.fluidInput == null) {
            return Optional.of(lastRecipe);
        }
        for (FoodFermenterRecipe recipe : getAllRecipes(level)) {
            if (recipe.matches(container, level) && recipe.fluidInput == null) {
                lastRecipe = recipe;
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(ItemStack stack, Level level) {
        if (lastRecipe != null && lastRecipe.container.getItem() == stack.getItem()
                && lastRecipe.fluidInput == null) {
            return Optional.of(lastRecipe);
        }
        for (FoodFermenterRecipe recipe : getAllRecipes(level)) {
            // Check if the stack matches the container item (simple heuristic)
            if (recipe.container.getItem() == stack.getItem() && recipe.fluidInput == null) {
                lastRecipe = recipe;
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(Container container, FluidStack fluid, Level level) {
        if (lastRecipe != null && lastRecipe.matches(container, level) && lastRecipe.fluidInput != null) {
            if (lastRecipe.fluidInput.test(fluid) && fluid.getAmount() >= lastRecipe.fluidInput.getAmount()) {
                return Optional.of(lastRecipe);
            }
        }

        List<FoodFermenterRecipe> allRecipes = getAllRecipes(level);

        for (FoodFermenterRecipe recipe : allRecipes) {
            boolean itemMatch = recipe.matches(container, level);

            boolean hasFluidReq = recipe.fluidInput != null;

            if (itemMatch) {
                if (hasFluidReq) {
                    boolean typeMatch = recipe.fluidInput.test(fluid);
                    boolean amountMatch = fluid.getAmount() >= recipe.fluidInput.getAmount();
                    if (typeMatch && amountMatch) {
                        lastRecipe = recipe;
                        return Optional.of(recipe);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(ItemStack stack, FluidStack fluid, Level level) {
        if (lastRecipe != null && lastRecipe.container.getItem() == stack.getItem()
                && lastRecipe.fluidInput != null) {
            if (lastRecipe.fluidInput.test(fluid)
                    && fluid.getAmount() >= lastRecipe.fluidInput.getAmount()) {
                return Optional.of(lastRecipe);
            }
        }
        for (FoodFermenterRecipe recipe : getAllRecipes(level)) {
            if (recipe.container.getItem() == stack.getItem() && recipe.fluidInput != null) {
                if (recipe.fluidInput.test(fluid)
                        && fluid.getAmount() >= recipe.fluidInput.getAmount()) {
                    lastRecipe = recipe;
                    return Optional.of(recipe);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public FoodFermenterRecipe byKey(ResourceLocation id, Level level) {
        FoodFermenterRecipe recipe = FoodFermenterRecipe.RECIPES.getById(level, id);
        if (recipe != null) {
            lastRecipe = recipe;
        }
        return recipe;
    }

    @Override
    public List<FoodFermenterRecipe> getAllRecipes(Level level) {
        return new ArrayList<>(FoodFermenterRecipe.RECIPES.getRecipes(level));
    }
}
