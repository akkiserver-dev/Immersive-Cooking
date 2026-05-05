package uk.akkiserver.immersivecooking.common.crafting.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public interface IMultiblockRecipeProvider<R extends Recipe<?>> {
    boolean canProvide();
    boolean hasMultiInput();
    Optional<RecipeHolder<R>> findRecipe(RecipeInput input, Level level);
    RecipeHolder<R> byKey(ResourceLocation id, Level level);
    List<RecipeHolder<R>> getAllRecipes(Level level);
}
