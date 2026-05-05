package uk.akkiserver.immersivecooking.common.crafting.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class SmokingRecipeProvider implements IMultiblockRecipeProvider<SmokingRecipe> {
    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public boolean hasMultiInput() {
        return false;
    }

    @Override
    public Optional<RecipeHolder<SmokingRecipe>> findRecipe(RecipeInput input, Level level) {
        return Optional.empty();
    }

    @Override
    public RecipeHolder<SmokingRecipe> byKey(ResourceLocation id, Level level) {
        return null;
    }

    @Override
    public List<RecipeHolder<SmokingRecipe>> getAllRecipes(Level level) {
        return List.of();
    }
}
