package uk.akkiserver.immersivecooking.common.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.CookpotScreen;
import uk.akkiserver.immersivecooking.client.gui.FoodFermenterScreen;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;
import uk.akkiserver.immersivecooking.common.util.Resource;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ICJEIPlugin implements IModPlugin {
    private static final ResourceLocation UID = Resource.mod("main");

    public ICJEIPlugin() {
        ImmersiveCooking.LOGGER.info("ICJEIPlugin Constructed!");
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        ImmersiveCooking.LOGGER.info("Registering categories to JEI...");
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new CookpotRecipeCategory(guiHelper));
        registry.addRecipeCategories(new FoodFermenterRecipeCategory(guiHelper));
        registry.addRecipeCategories(new SlicerRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ImmersiveCooking.LOGGER.debug("Registering recipes to JEI...");

        List<RecipeHolder<CookpotRecipe>> cookpotRecipes = new ArrayList<>(CookpotRecipe.RECIPES.values());
        ImmersiveCooking.LOGGER.debug("Found {} Cookpot recipes.", cookpotRecipes.size());
        registration.addRecipes(ICJEIRecipeTypes.COOKPOT, cookpotRecipes);

        List<RecipeHolder<FoodFermenterRecipe>> foodFermenterRecipes = new ArrayList<>(FoodFermenterRecipe.RECIPES.values());
        ImmersiveCooking.LOGGER.debug("Found {} Food Fermenter recipes.", foodFermenterRecipes.size());
        registration.addRecipes(ICJEIRecipeTypes.FOOD_FERMENTER, foodFermenterRecipes);

        List<RecipeHolder<SlicerRecipe>> slicerRecipes = new ArrayList<>(SlicerRecipe.RECIPES.values());
        ImmersiveCooking.LOGGER.debug("Found {} Slicer recipes.", slicerRecipes.size());
        registration.addRecipes(ICJEIRecipeTypes.SLICER, slicerRecipes);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        ImmersiveCooking.LOGGER.debug("Registering recipe transfer handlers to JEI...");
        registration.addRecipeTransferHandler(new GrillOvenRecipeTransferHandler(registration.getTransferHelper()), RecipeTypes.SMOKING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ImmersiveCooking.LOGGER.debug("Registering recipe catalysts to JEI...");
        registration.addRecipeCatalyst(ICContent.Multiblock.COOKPOT.iconStack(), ICJEIRecipeTypes.COOKPOT);
        registration.addRecipeCatalyst(ICContent.Multiblock.FOOD_FERMENTER.iconStack(), ICJEIRecipeTypes.FOOD_FERMENTER);
        registration.addRecipeCatalyst(ICContent.Multiblock.SLICER.iconStack(), ICJEIRecipeTypes.SLICER);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        ImmersiveCooking.LOGGER.debug("Registering gui handlers to JEI...");
        registration.addRecipeClickArea(CookpotScreen.class, 91, 19, 16, 12, ICJEIRecipeTypes.COOKPOT);
        //@TODO fix clicking areas
        registration.addRecipeClickArea(FoodFermenterScreen.class, 91, 19, 16, 12, ICJEIRecipeTypes.FOOD_FERMENTER);
    }
}
