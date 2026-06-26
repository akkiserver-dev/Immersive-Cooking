package uk.akkiserver.immersivecooking.common.compat.bnc;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.api.crafting.IRecipeConverter;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BnCFermentingRecipeConverter implements IRecipeConverter<KegFermentingRecipe, FoodFermenterRecipe> {
    private static final int DEFAULT_COOK_TIME = 200;
    private static final int DEFAULT_ENERGY = 800;

    /**
     * Cache of KEG_POURING recipes for the current RecipeManager instance, sorted with non-strict
     * recipes first (matching BnCClientRecipeUtils' ordering). Invalidated whenever the RecipeManager
     * instance changes (i.e. on every recipe reload), since RecipeManager is rebuilt wholesale on reload.
     */
    @Nullable
    private RecipeManager cachedManager;
    private List<KegPouringRecipe> cachedPouringRecipes = List.of();

    @Override
    public RecipeType<KegFermentingRecipe> sourceType() {
        return BnCRecipeTypes.FERMENTING;
    }

    @Override
    public Optional<RecipeHolder<FoodFermenterRecipe>> convert(
            RecipeHolder<KegFermentingRecipe> input,
            RecipeManager recipeManager,
            HolderLookup.Provider provider) {
        try {
            FoodFermenterRecipe recipe = toFoodFermenterRecipe(input, recipeManager, provider);
            return Optional.of(new RecipeHolder<>(input.id(), recipe));
        } catch (Exception e) {
            ImmersiveCooking.LOGGER.error("[BnC Compat] Failed to convert recipe {}", input.id(), e);
            return Optional.empty();
        }
    }

    private List<KegPouringRecipe> getPouringRecipes(RecipeManager recipeManager) {
        if (cachedManager != recipeManager) {
            cachedPouringRecipes = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING)
                    .stream()
                    .map(RecipeHolder::value)
                    .sorted(Comparator.comparing(KegPouringRecipe::isStrict))
                    .collect(Collectors.toList());
            cachedManager = recipeManager;
        }
        return cachedPouringRecipes;
    }

    private FoodFermenterRecipe toFoodFermenterRecipe(
            RecipeHolder<KegFermentingRecipe> holder,
            RecipeManager recipeManager,
            HolderLookup.Provider provider) {
        KegFermentingRecipe bncRecipe = holder.value();

        NonNullList<IngredientWithSize> inputs = bncRecipe.getIngredients().stream()
                .map(IngredientWithSize::new)
                .filter(ingredient -> !ingredient.hasNoMatchingItems())
                .collect(Collectors.toCollection(NonNullList::create));

        SizedFluidIngredient sizedFluidIngredient = bncRecipe.getFluidIngredient()
                .map(BnCCompat::toSizedFluidIngredient)
                .orElse(null);

        ItemStack output;
        ItemStack container;

        Optional<AbstractedFluidStack> fluidResult = bncRecipe.getResult().left();
        if (fluidResult.isPresent()) {
            AbstractedFluidStack resultFluid = fluidResult.get();

            Optional<KegPouringRecipe> pouringRecipe = getPouringRecipes(recipeManager)
                    .stream()
                    .filter(r -> r.getRawFluid().matches(resultFluid))
                    .findFirst();

            if (pouringRecipe.isEmpty()) {
                throw new IllegalStateException("No matching KEG_POURING recipe found for fluid result " + resultFluid.fluid());
            }

            KegPouringRecipe pouring = pouringRecipe.get();
            output = pouring.getOutput().copy();
            container = pouring.getContainer().copy();

            long fluidAmountMb = resultFluid.unit().convertToLoader(resultFluid.amount());
            long pourAmountMb = pouring.getUnit().convertToLoader(pouring.getRawFluid().amount());
            int count = (int) Math.max(1, fluidAmountMb / pourAmountMb);
            output = output.copyWithCount(count);
            container = container.copyWithCount(count);
        } else {
            output = bncRecipe.getResultItem(provider);
            container = ItemStack.EMPTY;
        }

        int fermentTime = bncRecipe.getFermentTime();
        if (fermentTime <= 0) {
            fermentTime = DEFAULT_COOK_TIME;
            ImmersiveCooking.LOGGER.warn("[BnC Compat] Recipe {} has zero ferment time, using default", holder.id());
        }

        return new FoodFermenterRecipe(inputs, sizedFluidIngredient, output, container, fermentTime, DEFAULT_ENERGY);
    }
}