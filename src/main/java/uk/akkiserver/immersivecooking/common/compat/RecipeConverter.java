package uk.akkiserver.immersivecooking.common.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public interface RecipeConverter<I extends Recipe<?>, R extends Recipe<?>> {
    RecipeType<I> sourceType();

    Optional<RecipeHolder<R>> convert(RecipeHolder<I> input, RecipeManager recipeManager, HolderLookup.Provider provider);

    default RecipeHolder<R> convertOrThrow(RecipeHolder<I> input, RecipeManager recipeManager, HolderLookup.Provider provider) {
        return convert(input, recipeManager, provider).orElseThrow();
    }
}
