package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.Optional;
import java.util.stream.Collectors;

public class SmokingRecipeProvider implements IMultiblockRecipeProvider<SmokingRecipe> {
    private static final RecipeManager.CachedCheck<Container, SmokingRecipe> smokingChecker = RecipeManager.createCheck(RecipeType.SMOKING);

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public boolean hasMultiInput() {
        return false;
    }

    @Override
    public Optional<SmokingRecipe> findRecipe(Container container, Level level) {
        return Optional.empty();
    }

    @Override
    public synchronized Optional<SmokingRecipe> findRecipe(ItemStack stack, Level level) {
        if (stack.isEmpty()) return Optional.empty();

        return smokingChecker.getRecipeFor(new SimpleContainer(stack), level);
    }

    @Override
    public SmokingRecipe byKey(ResourceLocation id, Level level) {
        return (SmokingRecipe) level.getRecipeManager().byKey(id).filter(r -> r instanceof SmokingRecipe).orElse(null);
    }
}
