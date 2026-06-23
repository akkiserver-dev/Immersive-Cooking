package uk.akkiserver.immersivecooking.common.compat.jei;

import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.CookpotScreen;
import uk.akkiserver.immersivecooking.client.gui.FoodFermenterScreen;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.CookpotLogic;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.FoodFermenterLogic;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.List;
import java.util.function.Predicate;

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
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering recipes to JEI...");
        List<CookpotRecipe> cookpotRecipes = CookpotRecipe.RECIPES.values().stream().map(RecipeHolder::value).toList();
        ImmersiveCooking.LOGGER.info("Found {} Cookpot recipes.", cookpotRecipes.size());
        registration.addRecipes(ICJEIRecipeTypes.COOKPOT, cookpotRecipes);
        List<FoodFermenterRecipe> foodFermenterRecipes = FoodFermenterRecipe.RECIPES.values().stream().map(RecipeHolder::value).toList();
        ImmersiveCooking.LOGGER.info("Found {} Food Fermenter recipes.", foodFermenterRecipes.size());
        registration.addRecipes(ICJEIRecipeTypes.FOOD_FERMENTER, foodFermenterRecipes);
        List<FoodProcessorRecipe> foodProcessorRecipes = FoodProcessorRecipe.RECIPES.values().stream().map(RecipeHolder::value).toList();
        ImmersiveCooking.LOGGER.info("Found {} Food Processor recipes.", foodProcessorRecipes.size());
        //registration.addRecipes(ICJEIRecipeTypes.FOOD_PROCESSOR, FoodProcessorRecipe);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering recipe transfer handlers to JEI...");
        registration.addRecipeTransferHandler(new GrillOvenRecipeTransferHandler(registration.getTransferHelper()), RecipeTypes.SMOKING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering recipe catalysts to JEI...");
        registration.addRecipeCatalyst(ICContent.Multiblock.COOKPOT.iconStack(), ICJEIRecipeTypes.COOKPOT);
        registration.addRecipeCatalyst(ICContent.Multiblock.FOOD_FERMENTER.iconStack(), ICJEIRecipeTypes.FOOD_FERMENTER);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering gui handlers to JEI...");
        registration.addRecipeClickArea(CookpotScreen.class, 91, 19, 16, 12, ICJEIRecipeTypes.COOKPOT);
        registration.addRecipeClickArea(FoodFermenterScreen.class, 91, 19, 16, 12, ICJEIRecipeTypes.FOOD_FERMENTER);
    }
}
