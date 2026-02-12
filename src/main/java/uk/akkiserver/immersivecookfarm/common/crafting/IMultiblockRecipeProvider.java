package uk.akkiserver.immersivecookfarm.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public interface IMultiblockRecipeProvider<R extends Recipe<?>> {
    boolean canProvide();
    boolean hasMultiInput();
    Optional<R> findRecipe(Container container, Level level);
    Optional<R> findRecipe(ItemStack stack, Level level);
    R byKey(ResourceLocation id, Level level);
    List<R> getAllRecipes(Level level);
}
