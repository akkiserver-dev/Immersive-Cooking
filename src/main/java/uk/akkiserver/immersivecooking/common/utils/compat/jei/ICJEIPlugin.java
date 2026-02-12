package uk.akkiserver.immersivecooking.common.utils.compat.jei;

import blusunrize.immersiveengineering.api.crafting.IJEIRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.client.gui.CookpotScreen;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        try {
            IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
            registry.addRecipeCategories(new CookpotRecipeCategory(guiHelper));
            ImmersiveCooking.LOGGER.info("Registered CookpotRecipeCategory.");
        } catch (Exception e) {
            ImmersiveCooking.LOGGER.error("Failed to register CookpotRecipeCategory!", e);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering recipes to JEI...");
        List<CookpotRecipe> recipes = new ArrayList<>(getFiltered(CookpotRecipe.RECIPES, IJEIRecipe::listInJEI));

        // Farmer's Delight integration
        if (ModList.get().isLoaded("farmersdelight")) {
            try {
                recipes.addAll(getFDRecipes());
            } catch (Exception e) {
                ImmersiveCooking.LOGGER.error("Failed to load Farmer's Delight recipes for JEI", e);
            }
        }

        ImmersiveCooking.LOGGER.info("Found " + recipes.size() + " Cookpot recipes (including FD).");
        registration.addRecipes(ICJEIRecipeTypes.COOKPOT, recipes);
    }

    private List<CookpotRecipe> getFDRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return Collections.emptyList();

        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
                .map(r -> {
                    NonNullList<IngredientWithSize> inputs = r
                            .getIngredients().stream()
                            .map(IngredientWithSize::new)
                            .collect(Collectors.toCollection(NonNullList::create));

                    ResourceLocation id = Resource.mod(r.getId().getPath());

                    return new CookpotRecipe(id, inputs,
                            r.getResultItem(level.registryAccess()), r.getOutputContainer(),
                            (int) (r.getCookTime() * 0.75),
                            r.getCookTime() * 4);
                })
                .toList();
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
        ImmersiveCooking.LOGGER.info("Registering recipe transfer handlers to JEI...");
        registration.addRecipeTransferHandler(new GrillOvenRecipeTransferHandler(registration.getTransferHelper()),
                RecipeTypes.SMOKING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering recipe catalysts to JEI...");
        registration.addRecipeCatalyst(ICContent.Multiblock.COOKPOT.iconStack(), ICJEIRecipeTypes.COOKPOT);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        ImmersiveCooking.LOGGER.info("Registering gui handlers to JEI...");
        registration.addRecipeClickArea(CookpotScreen.class, 91, 19, 16, 12, ICJEIRecipeTypes.COOKPOT);
    }
}
