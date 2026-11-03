package uk.akkiserver.immersivecooking.common.compat.jei;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;

public class ICJEIRecipeTypes {
    public static final RecipeType<RecipeHolder<CookpotRecipe>> COOKPOT = create(ICRecipes.Types.COOKPOT);

    public static final RecipeType<RecipeHolder<FoodFermenterRecipe>> FOOD_FERMENTER = create(ICRecipes.Types.FOOD_FERMENTER);

    public static final RecipeType<RecipeHolder<SlicerRecipe>> SLICER = create(ICRecipes.Types.SLICER);

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> RecipeType<RecipeHolder<T>> create(IERecipeTypes.TypeWithClass<T> type) {
        return new RecipeType<>(type.type().getId(), (Class<RecipeHolder<T>>) (Class<?>) RecipeHolder.class);
    }
}
