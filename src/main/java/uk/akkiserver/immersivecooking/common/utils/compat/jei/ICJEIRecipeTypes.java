package uk.akkiserver.immersivecooking.common.utils.compat.jei;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public class ICJEIRecipeTypes {
    public static final RecipeType<CookpotRecipe> COOKPOT = create(ICRecipes.Types.COOKPOT);

    private static <T extends Recipe<?>> RecipeType<T> create(IERecipeTypes.TypeWithClass<T> type) {
        return new RecipeType<>(type.type().getId(), type.recipeClass());
    }
}
