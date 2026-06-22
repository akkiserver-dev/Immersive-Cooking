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
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;

import java.util.Optional;
import java.util.stream.Collectors;

public class BnCFermentingRecipeConverter implements IRecipeConverter<KegFermentingRecipe, FoodFermenterRecipe> {
    private static final int DEFAULT_COOK_TIME = 200;
    private static final int DEFAULT_ENERGY = 800;

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

    private FoodFermenterRecipe toFoodFermenterRecipe(
            RecipeHolder<KegFermentingRecipe> holder,
            RecipeManager recipeManager,
            HolderLookup.Provider provider) {
        KegFermentingRecipe bncRecipe = holder.value();

        NonNullList<IngredientWithSize> inputs = bncRecipe.getIngredients().stream()
                .map(IngredientWithSize::new)
                .collect(Collectors.toCollection(NonNullList::create));

        SizedFluidIngredient sizedFluidIngredient = bncRecipe.getFluidIngredient()
                .map(BnCCompat::toSizedFluidIngredient)
                .orElse(null);

        ItemStack output = bncRecipe.getResultItem(provider);

        ItemStack finalOutput = output;
        ItemStack container = recipeManager.getAllRecipesFor(BnCRecipeTypes.KEG_POURING)
                .stream()
                .filter(r -> ItemStack.isSameItem(r.value().getResultItem(provider), finalOutput))
                .findFirst()
                .map(r -> r.value().getContainer())
                .orElse(ItemStack.EMPTY);

        Optional<AbstractedFluidStack> fluidResult = bncRecipe.getResult().left();
        if (fluidResult.isPresent() && container.is(BnCItems.TANKARD)) {
            AbstractedFluidStack fs = fluidResult.get();
            long fluidAmountMb = fs.unit().convertToLoader(fs.amount());
            int count = (int) Math.max(1, fluidAmountMb / 250);
            output = output.copyWithCount(count);
            container = container.copyWithCount(count);
        }

        int fermentTime = bncRecipe.getFermentTime();
        if (fermentTime <= 0) {
            fermentTime = DEFAULT_COOK_TIME;
            ImmersiveCooking.LOGGER.warn("[BnC Compat] Recipe {} has zero ferment time, using default", holder.id());
        }

        return new FoodFermenterRecipe(inputs, sizedFluidIngredient, output, container, fermentTime, DEFAULT_ENERGY);
    }
}