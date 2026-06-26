package uk.akkiserver.immersivecooking.common.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;

public class FoodProcessorRecipeCategory extends ICRecipeCategory<FoodProcessorRecipe> {
    public FoodProcessorRecipeCategory(IGuiHelper helper) {
        super(helper, ICJEIRecipeTypes.FOOD_PROCESSOR, "block." + ImmersiveCooking.MODID + ".food_processor");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, FoodProcessorRecipe foodProcessorRecipe, IFocusGroup iFocusGroup) {

    }
}
