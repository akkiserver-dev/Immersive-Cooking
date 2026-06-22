package uk.akkiserver.immersivecooking.api.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public interface IRecipeConverter<Input extends Recipe<?>, Result extends Recipe<?>> {
    RecipeType<Input> sourceType();

    Optional<RecipeHolder<Result>> convert(RecipeHolder<Input> input, RecipeManager recipeManager, HolderLookup.Provider provider);
}
