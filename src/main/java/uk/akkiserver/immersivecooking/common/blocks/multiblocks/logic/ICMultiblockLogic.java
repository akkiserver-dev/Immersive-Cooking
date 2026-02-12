package uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import uk.akkiserver.immersivecooking.common.crafting.IMultiblockRecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ICMultiblockLogic<S extends IMultiblockState, R extends Recipe<?>> implements IMultiblockLogic<S> {
    protected final List<IMultiblockRecipeProvider<R>> recipeProviders = new ArrayList<>();

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
            if (provider.canProvide() && provider.hasMultiInput()) {
                return provider.byKey(id, level);
            }
        }
        return null;
    }

    public List<R> getAllProvidedRecipes(Level level) {
        List<R> recipes = new ArrayList<>();
        for (var provider : recipeProviders) {
            recipes.addAll(provider.getAllRecipes(level));
        }
        return recipes;
    }

    public ItemStack getRecipeResult(R recipe, Level level) {
        return recipe.getResultItem(level.registryAccess());
    }
}
