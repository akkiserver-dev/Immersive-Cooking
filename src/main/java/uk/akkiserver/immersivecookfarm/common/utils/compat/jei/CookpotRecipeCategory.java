package uk.akkiserver.immersivecookfarm.common.utils.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

import java.util.Arrays;

public class CookpotRecipeCategory extends ICRecipeCategory<CookpotRecipe> {

    public CookpotRecipeCategory(IGuiHelper helper) {
        super(helper, ICJEIRecipeTypes.COOKPOT, "block." + ImmersiveCookFarm.MODID + ".cookpot");
        ResourceLocation background = Resource.mod("textures/gui/cookpot.png");
        setBackground(helper.createDrawable(background, 6, 12, 126, 59));
        setIcon(ICContent.Multiblock.COOKPOT.iconStack());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookpotRecipe recipe, IFocusGroup focuses) {
        // Inputs 3x2 grid starting at (23,19) relative to GUI -> (17,7) relative to
        // helper
        for (int i = 0; i < recipe.inputs.size(); i++) {
            if (i >= 6)
                break;
            int x = 17 + (i % 3) * 18;
            int y = 7 + (i / 3) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStacks(Arrays.asList(recipe.inputs.get(i).getMatchingStacks()));
        }

        // Output slot at (113,53) relative to GUI -> (107,41) relative to helper
        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 41)
                .addItemStack(recipe.itemOutput);

        // Container slot at (91,53) relative to GUI -> (85,41) relative to helper
        if (!recipe.container.isEmpty())
            builder.addSlot(RecipeIngredientRole.INPUT, 85, 41)
                    .addItemStack(recipe.container);
    }
}
