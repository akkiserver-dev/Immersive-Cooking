package uk.akkiserver.immersivecooking.common.crafting.providers;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMultiblockRecipeProvider<R extends Recipe<RecipeInput>> implements IMultiblockRecipeProvider<R> {
    private final Lazy<RecipeManager.CachedCheck<RecipeInput, R>> cachedCheck = Lazy
            .of(() -> RecipeManager.createCheck(getRecipeType()));

    protected abstract RecipeType<R> getRecipeType();
    protected abstract Class<R> getRecipeClass();

    @Override
    public Optional<RecipeHolder<R>> findRecipe(RecipeInput input, Level level) {
        return cachedCheck.get().getRecipeFor(input, level);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeHolder<R> byKey(ResourceLocation id, Level level) {
        return (RecipeHolder<R>) level.getRecipeManager().byKey(id)
                .filter(holder -> getRecipeClass().isInstance(holder.value()))
                .orElse(null);
    }

    @Override
    public List<RecipeHolder<R>> getAllRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(getRecipeType());
    }
}
