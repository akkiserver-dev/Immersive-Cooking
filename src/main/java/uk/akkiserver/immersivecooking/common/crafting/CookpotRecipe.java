package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.compat.RecipeConverter;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import java.util.*;

public class CookpotRecipe extends MultiblockRecipe {
    public static final Map<ResourceLocation, RecipeHolder<CookpotRecipe>> RECIPES = new HashMap<>();
    public static final List<RecipeConverter<?, CookpotRecipe>> RECIPE_CONVERTERS = new ArrayList<>();

    public final NonNullList<IngredientWithSize> inputs;
    public final ItemStack result;
    public final ItemStack container;

    public CookpotRecipe(NonNullList<IngredientWithSize> inputs, ItemStack result, ItemStack container, int cookTime, int energy) {
        super(TagOutput.EMPTY, ICRecipes.Types.COOKPOT, cookTime, energy, ICRecipes.NO_MULTIPLIER);

        this.inputs = inputs;
        this.result = result;
        this.container = container;

        this.setInputListWithSizes(new ArrayList<>(this.inputs));
        this.outputList = new TagOutputList(new TagOutput(result));
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
        Map<ResourceLocation, RecipeHolder<CookpotRecipe>> newRecipes = new HashMap<>(recipes);

        for (RecipeConverter<?, CookpotRecipe> converter : RECIPE_CONVERTERS) {
            ImmersiveCooking.LOGGER.info("[CookpotRecipe] Converting recipes from {}...", BuiltInRegistries.RECIPE_TYPE.getKey(converter.sourceType()));
            for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
                tryConvert(converter, holder, recipeManager, provider, newRecipes);
            }
        }

        RECIPES.clear();
        RECIPES.putAll(newRecipes);
    }

    @SuppressWarnings("unchecked")
    private static <I extends Recipe<?>> void tryConvert(RecipeConverter<I, CookpotRecipe> converter, RecipeHolder<?> holder, RecipeManager recipeManager, HolderLookup.Provider provider, Map<ResourceLocation, RecipeHolder<CookpotRecipe>> out) {
        if (holder.value().getType() != converter.sourceType()) return;

        converter.convert((RecipeHolder<I>) holder, recipeManager, provider).ifPresent(r -> out.put(r.id(), r));
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
        return findInputSlots(inv) != null;
    }

    /**
     * Returns one inventory slot per ingredient, in recipe order. As in the
     * original cooking pot, every occupied input slot must match one ingredient.
     * Stack counts allow repeated batches, but cannot substitute for extra slots.
     */
    public int @Nullable [] findInputSlots(RecipeInput inv) {
        List<ItemStack> stacks = new ArrayList<>();
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < Math.min(6, inv.size()); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
                slots.add(i);
            }
        }

        int[] ingredientsBySlot = RecipeMatcher.findMatches(stacks, inputs);
        if (ingredientsBySlot == null || ingredientsBySlot.length == 0)
            return null;

        int[] slotsByIngredient = new int[inputs.size()];
        for (int i = 0; i < ingredientsBySlot.length; i++)
            slotsByIngredient[ingredientsBySlot[i]] = slots.get(i);
        return slotsByIngredient;
    }
}
