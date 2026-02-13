package uk.akkiserver.immersivecooking.common.utils.compat.jei;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.Arrays;

public class FoodFermenterRecipeCategory extends ICRecipeCategory<FoodFermenterRecipe> {

    public FoodFermenterRecipeCategory(IGuiHelper helper) {
        super(helper, ICJEIRecipeTypes.FOOD_FERMENTER, "block." + ImmersiveCooking.MODID + ".food_fermenter");
        ResourceLocation background = Resource.mod("textures/gui/food_fermenter.png");
        setBackground(helper.createDrawable(background, 6, 6, 150, 68));
        setIcon(ICContent.Multiblock.FOOD_FERMENTER.iconStack());
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull FoodFermenterRecipe recipe, @NotNull IFocusGroup focuses) {
        if (recipe.fluidInput != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 14)
                    .setFluidRenderer(12000, false, 16, 47)
                    .addIngredients(ForgeTypes.FLUID_STACK, recipe.fluidInput.getMatchingFluidStacks());
        }

        for (int i = 0; i < recipe.inputs.size(); i++) {
            if (i >= 6) break;
            int x = 56 + (i % 3) * 18;
            int y = 20 + (i / 3) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStacks(Arrays.asList(recipe.inputs.get(i).getMatchingStacks()));
        }

        if (!recipe.container.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 127, 9)
                    .addItemStack(recipe.container);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 127, 48)
                .addItemStack(recipe.itemOutput);
    }

    @Override
    public void draw(FoodFermenterRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        String time = I18n.get("desc.immersiveengineering.info.seconds", Utils.formatDouble(recipe.getTotalProcessTime() / 20f, "#.##"));
        graphics.drawString(
                ClientUtils.font(),
                time,
                1,
                1,
                0xFFFFFF,
                false);
    }
}
