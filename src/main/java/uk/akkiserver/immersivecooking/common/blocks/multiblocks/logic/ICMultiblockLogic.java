package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.providers.IFluidContainingMultiblockRecipeProvider;
import uk.akkiserver.immersivecooking.common.crafting.providers.IMultiblockRecipeProvider;

import java.util.*;

public abstract class ICMultiblockLogic<S extends IMultiblockState, R extends Recipe<?>>
        implements IMultiblockLogic<S> {
    protected final List<IMultiblockRecipeProvider<R>> recipeProviders = new ArrayList<>();

    public Optional<R> findRecipe(ItemStack stack, FluidStack fluid, Level level) {
        for (var normalProvider : recipeProviders) {
            if (normalProvider.canProvide()
                    && normalProvider instanceof IFluidContainingMultiblockRecipeProvider<R> provider) {
                Optional<R> recipe = provider.findRecipe(stack, fluid, level);
                if (recipe.isPresent()) {
                    return recipe;
                }
            }
        }
        return Optional.empty();
    }

    public Optional<R> findRecipe(Container container, FluidStack fluid, Level level) {
        for (var normalProvider : recipeProviders) {
            if (normalProvider.canProvide()
                    && normalProvider instanceof IFluidContainingMultiblockRecipeProvider<R> provider) {
                Optional<R> recipe = provider.findRecipe(container, fluid, level);
                if (recipe.isPresent()) {
                    return recipe;
                }
            }
        }
        return Optional.empty();
    }

    public Optional<R> findRecipe(ItemStack stack, Level level) {
        for (var provider : recipeProviders) {
            if (provider.canProvide()) {
                Optional<R> recipe = provider.findRecipe(stack, level);
                if (recipe.isPresent()) {
                    return recipe;
                }
            }
        }
        return Optional.empty();
    }

    public Optional<R> findRecipe(Container container, Level level) {
        for (var provider : recipeProviders) {
            if (provider.canProvide() && provider.hasMultiInput()) {
                Optional<R> recipe = provider.findRecipe(container, level);
                if (recipe.isPresent()) {
                    return recipe;
                }
            }
        }
        return Optional.empty();
    }

    public R byKey(ResourceLocation id, Level level) {
        for (var provider : recipeProviders) {
            if (provider.canProvide()) {
                R recipe = provider.byKey(id, level);
                if (recipe != null) {
                    return recipe;
                }
            }
        }
        return null;
    }

    public List<R> getAllProvidedRecipes(Level level) {
        List<R> recipes = new ArrayList<>();
        for (var provider : recipeProviders) {
            if (provider.canProvide()) {
                recipes.addAll(provider.getAllRecipes(level));
            }
        }
        return recipes;
    }

    public ItemStack getRecipeResult(R recipe, Level level) {
        return recipe.getResultItem(level.registryAccess());
    }
}
