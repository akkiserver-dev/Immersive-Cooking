package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * based on Immersive Petroleum.
 * Thanks to original mod author TwistedGate
 */
public class RecipeReloadListener implements ResourceManagerReloadListener {
    private final ReloadableServerResources serverResources;

    public RecipeReloadListener(ReloadableServerResources serverResources) {
        this.serverResources = serverResources;
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager){
        if (serverResources != null) {
            loadRecipes(serverResources);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void recipesUpdated(RecipesUpdatedEvent event) {
        if (!Minecraft.getInstance().hasSingleplayerServer() && serverResources != null) {
            loadRecipes(serverResources);
        }
    }

    private static void loadRecipes(ReloadableServerResources serverResources) {
        RecipeManager recipeManager = serverResources.getRecipeManager();
        HolderLookup.Provider provider = serverResources.getRegistryLookup();

        Collection<RecipeHolder<?>> recipes = recipeManager.getRecipes();

        if (recipes.isEmpty()) return;

        CookpotRecipe.updateRecipes(recipeManager, provider, indexRecipes(recipes, ICRecipes.Types.COOKPOT.get()));
        FoodProcessorRecipe.updateRecipes(recipeManager, provider, indexRecipes(recipes, ICRecipes.Types.FOOD_PROCESSOR.get()));
        FoodFermenterRecipe.updateRecipes(recipeManager, provider, indexRecipes(recipes, ICRecipes.Types.FOOD_FERMENTER.get()));
    }

    @SuppressWarnings("unchecked")
    private static <R extends Recipe<?>, H extends RecipeHolder<R>> Map<ResourceLocation, H> indexRecipes(Collection<RecipeHolder<?>> recipes, RecipeType<R> recipeType) {
        return (Map<ResourceLocation, H>) recipes.stream()
                .filter(holder -> holder.value().getType() == recipeType)
                .collect(Collectors.toMap(RecipeHolder::id, holder -> (RecipeHolder<R>) holder));
    }
}
