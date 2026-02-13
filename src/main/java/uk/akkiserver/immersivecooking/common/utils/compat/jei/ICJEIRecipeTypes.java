package uk.akkiserver.immersivecooking.common.utils.compat.jei;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;

public class ICJEIRecipeTypes {
    public static final RecipeType<CookpotRecipe> COOKPOT = create(ICRecipes.Types.COOKPOT);
    public static final RecipeType<FoodFermenterRecipe> FOOD_FERMENTER = create(ICRecipes.Types.FOOD_FERMENTER);

    private static <T extends Recipe<?>> RecipeType<T> create(IERecipeTypes.TypeWithClass<T> type) {
        return new RecipeType<>(type.type().getId(), type.recipeClass());
    }
}
