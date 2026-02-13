package uk.akkiserver.immersivecooking.common.crafting.providers;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.utils.Compat;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FCCookpotRecipeProvider implements IMultiblockRecipeProvider<CookpotRecipe> {
    private static final int DEFAULT_COOK_TIME = 200;
    private static final int DEFAULT_ENERGY = 800;

    @Override
    public boolean canProvide() {
        return Compat.isFarmCharmInstalled();
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(Container container, Level level) {
        if (!canProvide() || container.isEmpty())
            return Optional.empty();

        RecipeWrapper wrapper;
        if (container instanceof RecipeWrapper rw) {
            wrapper = rw;
        } else {
            wrapper = new RecipeWrapper(new InvWrapper(container));
        }

        return level.getRecipeManager()
                .getRecipeFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get(), wrapper, level)
                .map(facRecipe -> toCookpotRecipe(facRecipe, level));
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(ItemStack stack, Level level) {
        if (!canProvide() || stack.isEmpty())
            return Optional.empty();

        return level.getRecipeManager()
                .getAllRecipesFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get())
                .stream()
                .filter(r -> ItemStack.isSameItem(r.getResultItem(level.registryAccess()), stack))
                .findFirst()
                .map(facRecipe -> toCookpotRecipe(facRecipe, level));
    }

    @Override
    public CookpotRecipe byKey(ResourceLocation id, Level level) {
        CookpotRecipe cached = CookpotRecipe.RECIPES.getById(level, id);
        if (cached != null)
            return cached;

        ResourceLocation facId = id;
        if (id.getNamespace().equals("immersivecooking")) {
            facId = ResourceLocation.fromNamespaceAndPath("farm_and_charm", id.getPath());
        }

        return level.getRecipeManager().byKey(facId)
                .filter(r -> r instanceof CookingPotRecipe)
                .map(r -> toCookpotRecipe((CookingPotRecipe) r, level))
                .orElse(null);
    }

    @Override
    public List<CookpotRecipe> getAllRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get()).stream()
                .map(r -> toCookpotRecipe(r, level))
                .collect(Collectors.toList());
    }

    private CookpotRecipe toCookpotRecipe(CookingPotRecipe facRecipe, Level level) {
        NonNullList<IngredientWithSize> inputs = facRecipe.getIngredients().stream()
                .map(IngredientWithSize::new)
                .collect(Collectors.toCollection(NonNullList::create));

        ItemStack output = facRecipe.getResultItem(level.registryAccess());

        ItemStack container = facRecipe.isContainerRequired()
                ? facRecipe.getContainerItem()
                : ItemStack.EMPTY;

        ResourceLocation id = Resource.mod(facRecipe.getId().getPath());

        return new CookpotRecipe(
                id,
                inputs,
                output,
                container,
                DEFAULT_COOK_TIME,
                DEFAULT_ENERGY);
    }
}