package uk.akkiserver.immersivecooking.common.compat.farmcharm;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import uk.akkiserver.immersivecooking.api.crafting.IRecipeConverter;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FarmCharmCookpotRecipeConverter {
    private static final int DEFAULT_COOK_TIME = 200;
    private static final int DEFAULT_ENERGY = 800;

    public static void addAll(List<IRecipeConverter<?, CookpotRecipe>> converters) {
        converters.add(new Cookpot());
        converters.add(new Roaster());
        converters.add(new Stove());
    }

    public static final class Cookpot implements IRecipeConverter<CookingPotRecipe, CookpotRecipe> {
        @Override
        public RecipeType<CookingPotRecipe> sourceType() {
            return RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get();
        }

        @Override
        public Optional<RecipeHolder<CookpotRecipe>> convert(RecipeHolder<CookingPotRecipe> input, RecipeManager recipeManager, HolderLookup.Provider provider) {
            CookingPotRecipe recipe = input.value();
            NonNullList<IngredientWithSize> inputs = recipe.getIngredients().stream()
                    .map(IngredientWithSize::new)
                    .collect(Collectors.toCollection(NonNullList::create));

            ItemStack output = recipe.getResultItem(provider);

            ItemStack container = recipe.isContainerRequired()
                    ? recipe.getContainerItem()
                    : ItemStack.EMPTY;

            ResourceLocation id = Resource.wrapFor(recipe.getId());

            return Optional.of(new RecipeHolder<>(id, new CookpotRecipe(
                    inputs,
                    output,
                    container,
                    DEFAULT_COOK_TIME,
                    DEFAULT_ENERGY)));
        }
    }

    public static final class Roaster implements IRecipeConverter<RoasterRecipe, CookpotRecipe> {
        @Override
        public RecipeType<RoasterRecipe> sourceType() {
            return RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get();
        }

        @Override
        public Optional<RecipeHolder<CookpotRecipe>> convert(RecipeHolder<RoasterRecipe> input, RecipeManager recipeManager, HolderLookup.Provider provider) {
            RoasterRecipe recipe = input.value();
            NonNullList<IngredientWithSize> inputs = recipe.getIngredients().stream()
                    .map(IngredientWithSize::new)
                    .collect(Collectors.toCollection(NonNullList::create));

            ItemStack output = recipe.getResultItem(provider);

            ResourceLocation id = Resource.wrapFor(recipe.getId());

            return Optional.of(new RecipeHolder<>(id, new CookpotRecipe(
                    inputs,
                    output,
                    recipe.getContainer(),
                    DEFAULT_COOK_TIME,
                    DEFAULT_ENERGY)));
        }
    }

    public static final class Stove implements IRecipeConverter<StoveRecipe, CookpotRecipe> {
        @Override
        public RecipeType<StoveRecipe> sourceType() {
            return RecipeTypeRegistry.STOVE_RECIPE_TYPE.get();
        }

        @Override
        public Optional<RecipeHolder<CookpotRecipe>> convert(RecipeHolder<StoveRecipe> input, RecipeManager recipeManager, HolderLookup.Provider provider) {
            StoveRecipe recipe = input.value();
            NonNullList<IngredientWithSize> inputs = recipe.getIngredients().stream()
                    .map(IngredientWithSize::new)
                    .collect(Collectors.toCollection(NonNullList::create));

            ItemStack output = recipe.getResultItem(provider);

            ResourceLocation id = Resource.wrapFor(recipe.getId());

            return Optional.of(new RecipeHolder<>(id, new CookpotRecipe(
                    inputs,
                    output,
                    ItemStack.EMPTY,
                    DEFAULT_COOK_TIME,
                    DEFAULT_ENERGY)));
        }
    }
}
