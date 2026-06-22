package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import uk.akkiserver.immersivecooking.api.crafting.IRecipeConverter;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import java.util.*;

public class CookpotRecipe extends MultiblockRecipe {
    public static final Map<ResourceLocation, RecipeHolder<CookpotRecipe>> RECIPES = new HashMap<>();
    public static final List<IRecipeConverter<?, CookpotRecipe>> RECIPE_CONVERTERS = new ArrayList<>();

    public final NonNullList<IngredientWithSize> inputs;
    public final ItemStack result;
    public final ItemStack container;

    public CookpotRecipe(NonNullList<IngredientWithSize> inputs, ItemStack result, ItemStack container, int cookTime, int energy) {
        super(
                TagOutput.EMPTY,
                ICRecipes.Types.COOKPOT,
                cookTime,
                energy,
                ICRecipes.NO_MULTIPLIER
        );
        this.inputs = inputs;
        this.result = result;
        this.container = container;

        this.setInputListWithSizes(new ArrayList<>(this.inputs));
    }

    public static Optional<RecipeHolder<CookpotRecipe>> findRecipe(RecipeInput input, Level level) {
        return RECIPES.values().stream()
                .filter(h -> h.value().matches(input, level))
                .findFirst();
    }

    public static Optional<RecipeHolder<CookpotRecipe>> findRecipeByOutput(ItemStack output) {
        return RECIPES.values().stream()
                .filter(h -> ItemStack.isSameItem(h.value().result, output))
                .findFirst();
    }

    public static void updateRecipes(RecipeManager recipeManager, HolderLookup.Provider provider, Map<ResourceLocation, RecipeHolder<CookpotRecipe>> recipes) {
        Map<ResourceLocation, RecipeHolder<CookpotRecipe>> newRecipes = new HashMap<>();

        for (IRecipeConverter<?, CookpotRecipe> converter : RECIPE_CONVERTERS) {
            for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
                tryConvert(converter, holder, recipeManager, provider, newRecipes);
            }
        }

        RECIPES.clear();
        RECIPES.putAll(newRecipes);
    }

    private static <I extends Recipe<?>> void tryConvert(
            IRecipeConverter<I, CookpotRecipe> converter,
            RecipeHolder<?> holder,
            RecipeManager recipeManager,
            HolderLookup.Provider provider,
            Map<ResourceLocation, RecipeHolder<CookpotRecipe>> out
    ) {
        if (holder.value().getType() != converter.sourceType()) return;

        @SuppressWarnings("unchecked")
        RecipeHolder<I> cast = (RecipeHolder<I>) holder;

        converter.convert(cast, recipeManager, provider)
                .ifPresent(r -> out.put(r.id(), r));
    }

    public NonNullList<IngredientWithSize> getInputs() {
        return inputs;
    }

    public ItemStack getContainer() {
        return container;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.COOKPOT.get();
    }

    @Override
    public boolean matches(RecipeInput inv, Level level) {
        java.util.List<ItemStack> inventoryCopy = new java.util.ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                inventoryCopy.add(stack.copy());
            }
        }

        for (IngredientWithSize required : this.inputs) {
            int amountNeeded = required.getCount();

            java.util.Iterator<ItemStack> it = inventoryCopy.iterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();

                if (required.test(stack)) {
                    int amountTaken = Math.min(amountNeeded, stack.getCount());

                    amountNeeded -= amountTaken;
                    stack.shrink(amountTaken);

                    if (stack.isEmpty()) {
                        it.remove();
                    }

                    if (amountNeeded <= 0) {
                        break;
                    }
                }
            }

            if (amountNeeded > 0) {
                return false;
            }
        }

        return true;
    }
}