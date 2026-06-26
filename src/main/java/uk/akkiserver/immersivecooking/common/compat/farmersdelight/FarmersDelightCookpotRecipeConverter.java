package uk.akkiserver.immersivecooking.common.compat.farmersdelight;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import uk.akkiserver.immersivecooking.common.compat.IRecipeConverter;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.Optional;
import java.util.stream.Collectors;

public class FarmersDelightCookpotRecipeConverter implements IRecipeConverter<CookingPotRecipe, CookpotRecipe> {
    @Override
    public RecipeType<CookingPotRecipe> sourceType() {
        return ModRecipeTypes.COOKING.get();
    }

    @Override
    public Optional<RecipeHolder<CookpotRecipe>> convert(RecipeHolder<CookingPotRecipe> input, RecipeManager recipeManager, HolderLookup.Provider provider) {
        CookingPotRecipe recipe = input.value();

        NonNullList<IngredientWithSize> inputs = recipe.getIngredients().stream()
                .map(IngredientWithSize::new)
                .collect(Collectors.toCollection(NonNullList::create));

        ItemStack output = recipe.getResultItem(provider);

        ResourceLocation id = Resource.wrapFor(input.id());

        return Optional.of(new RecipeHolder<>(id, new CookpotRecipe(
                inputs,
                output,
                recipe.getOutputContainer(),
                (int) (recipe.getCookTime() * 0.75),
                recipe.getCookTime() * 8)));
    }
}
