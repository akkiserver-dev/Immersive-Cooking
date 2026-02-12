package uk.akkiserver.immersivecookfarm.common.utils.compat.jei;

import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;
import uk.akkiserver.immersivecookfarm.client.gui.CookpotScreen;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.blocks.multiblocks.logic.CookpotLogic;
import uk.akkiserver.immersivecookfarm.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

import java.util.List;
import java.util.function.Predicate;

@JeiPlugin
public class ICJEIPlugin implements IModPlugin {
    private static final ResourceLocation UID = Resource.mod("main");

    public ICJEIPlugin() {
        ImmersiveCookFarm.LOGGER.info("ICJEIPlugin Constructed!");
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        ImmersiveCookFarm.LOGGER.info("Registering categories to JEI...");
        try {
            IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
            registry.addRecipeCategories(new CookpotRecipeCategory(guiHelper));
            ImmersiveCookFarm.LOGGER.info("Registered CookpotRecipeCategory.");
        } catch (Exception e) {
            ImmersiveCookFarm.LOGGER.error("Failed to register CookpotRecipeCategory!", e);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ImmersiveCookFarm.LOGGER.info("Registering recipes to JEI...");
        List<CookpotRecipe> recipes = ((CookpotLogic) ICContent.Multiblock.COOKPOT.logic()).getAllProvidedRecipes(Minecraft.getInstance().level);
        ImmersiveCookFarm.LOGGER.info("Found {} Cookpot recipes.", recipes.size());
        registration.addRecipes(ICJEIRecipeTypes.COOKPOT, recipes);
    }

    private <T extends Recipe<?>> List<T> getRecipes(CachedRecipeList<T> cachedList) {
        return getFiltered(cachedList, $ -> true);
    }

    private <T extends Recipe<?>> List<T> getFiltered(CachedRecipeList<T> cachedList, Predicate<T> include) {
        return cachedList.getRecipes(Minecraft.getInstance().level).stream()
                .filter(include)
                .toList();
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        ImmersiveCookFarm.LOGGER.info("Registering recipe transfer handlers to JEI...");
        registration.addRecipeTransferHandler(new GrillOvenRecipeTransferHandler(registration.getTransferHelper()),
                RecipeTypes.SMOKING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ImmersiveCookFarm.LOGGER.info("Registering recipe catalysts to JEI...");
        registration.addRecipeCatalyst(ICContent.Multiblock.COOKPOT.iconStack(), ICJEIRecipeTypes.COOKPOT);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        ImmersiveCookFarm.LOGGER.info("Registering gui handlers to JEI...");
        registration.addRecipeClickArea(CookpotScreen.class, 91, 19, 16, 12, ICJEIRecipeTypes.COOKPOT);
    }
}
