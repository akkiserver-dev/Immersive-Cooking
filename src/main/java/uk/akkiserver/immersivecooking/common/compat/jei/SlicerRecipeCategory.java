package uk.akkiserver.immersivecooking.common.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;

public class SlicerRecipeCategory extends ICRecipeCategory<SlicerRecipe> {
    private final int footerY;

    public SlicerRecipeCategory(IGuiHelper helper) {
        super(helper, ICJEIRecipeTypes.SLICER, "block.immersivecooking.slicer");
        int maxByproducts = SlicerRecipe.RECIPES.values().stream()
                .mapToInt(holder -> holder.value().byproducts.size()).max().orElse(0);
        footerY = Math.max(50, 32 + 18 * ((maxByproducts + 1) / 2)) + 3;
        setBackground(helper.createBlankDrawable(100, footerY + 11));
        setIcon(ICContent.Multiblock.SLICER.iconStack());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SlicerRecipe> holder, IFocusGroup focuses) {
        SlicerRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 13).setStandardSlotBackground()
                .addItemStacks(recipe.input.getMatchingStackList());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 13).setStandardSlotBackground().addItemStack(recipe.result);
        for (int i = 0; i < recipe.byproducts.size(); i++) {
            var byproduct = recipe.byproducts.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 65 + i % 2 * 18, 33 + i / 2 * 18)
                    .setStandardSlotBackground().setSlotName("byproduct_" + i)
                    .addItemStack(byproduct.stack().get())
                    .addRichTooltipCallback((slot, tooltip) -> {
                        tooltip.add(Component.translatable("jei.immersivecooking.byproduct"));
                        if (byproduct.chance() < 1)
                            tooltip.add(Component.translatable("jei.immersivecooking.chance",
                                    String.format(java.util.Locale.ROOT, "%.1f", byproduct.chance() * 100)));
                    });
        }
    }

    @Override
    public void draw(@NotNull RecipeHolder<SlicerRecipe> recipe, @NotNull IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.pose().pushPose();
        graphics.pose().scale(3, 3, 1);
        getIcon().draw(graphics, 8, 0);
        graphics.pose().popPose();
    }
}
