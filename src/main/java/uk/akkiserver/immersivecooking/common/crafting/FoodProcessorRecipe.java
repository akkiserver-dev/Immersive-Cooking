package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.compat.IRecipeConverter;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import java.util.*;

public class FoodProcessorRecipe extends MultiblockRecipe {
    public static final Map<ResourceLocation, RecipeHolder<FoodProcessorRecipe>> RECIPES = new HashMap<>();
    public static final List<IRecipeConverter<?, FoodProcessorRecipe>> RECIPE_CONVERTERS = new ArrayList<>();

    public NonNullList<IngredientWithSize> inputs; // 8 slots
    @Nullable
    public final SizedFluidIngredient fluidInput;
    public final ItemStack result;

    public FoodProcessorRecipe(NonNullList<IngredientWithSize> inputs,
                               @Nullable SizedFluidIngredient fluidInput,
                               ItemStack result, int time, int energy) {
        super(
                TagOutput.EMPTY,
                ICRecipes.Types.FOOD_PROCESSOR,
                time,
                energy,
                ICRecipes.NO_MULTIPLIER
        );
        this.fluidInput = fluidInput;
        this.result = result;
        this.inputs = inputs;

        this.setInputListWithSizes(new ArrayList<>(this.inputs));
        this.outputList = new TagOutputList(new TagOutput(result));
    }

    public static void updateRecipes(RecipeManager recipeManager, HolderLookup.Provider provider, Map<ResourceLocation, RecipeHolder<FoodProcessorRecipe>> recipes) {
        Map<ResourceLocation, RecipeHolder<FoodProcessorRecipe>> newRecipes = new HashMap<>(recipes);

        for (IRecipeConverter<?, FoodProcessorRecipe> converter : RECIPE_CONVERTERS) {
            for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
                tryConvert(converter, holder, recipeManager, provider, newRecipes);
            }
        }

        RECIPES.clear();
        RECIPES.putAll(newRecipes);
    }

    @Override
    public boolean matches(RecipeInput inv, Level level) {
        List<ItemStack> inventoryCopy = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                inventoryCopy.add(stack.copy());
            }
        }

        for (IngredientWithSize required : this.inputs) {
            int amountNeeded = required.getCount();

            Iterator<ItemStack> it = inventoryCopy.iterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();
                if (required.test(stack)) {
                    int taken = Math.min(amountNeeded, stack.getCount());
                    amountNeeded -= taken;
                    stack.shrink(taken);
                    if (stack.isEmpty()) it.remove();
                    if (amountNeeded <= 0) break;
                }
            }

            if (amountNeeded > 0) return false;
        }
        return true;
    }

    public static Optional<RecipeHolder<FoodProcessorRecipe>> findRecipe(RecipeInput input, Level level) {
        return RECIPES.values().stream()
                .filter(h -> h.value().fluidInput == null)
                .filter(h -> h.value().matches(input, level))
                .findFirst();
    }

    public static Optional<RecipeHolder<FoodProcessorRecipe>> findRecipe(RecipeInput input, FluidStack fluid, Level level) {
        return RECIPES.values().stream()
                .filter(h -> h.value().fluidInput != null)
                .filter(h -> h.value().matches(input, level))
                .filter(h -> {
                    SizedFluidIngredient fi = h.value().fluidInput;
                    return fi.ingredient().test(fluid) && fluid.getAmount() >= fi.amount();
                })
                .findFirst();
    }

    public static Optional<RecipeHolder<FoodProcessorRecipe>> findRecipeByOutput(ItemStack output) {
        return RECIPES.values().stream()
                .filter(h -> ItemStack.isSameItem(h.value().result, output))
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    private static <I extends Recipe<?>> void tryConvert(
            IRecipeConverter<I, FoodProcessorRecipe> converter,
            RecipeHolder<?> holder,
            RecipeManager recipeManager,
            HolderLookup.Provider provider,
            Map<ResourceLocation, RecipeHolder<FoodProcessorRecipe>> out
    ) {
        if (holder.value().getType() != converter.sourceType()) return;

        converter.convert((RecipeHolder<I>) holder, recipeManager, provider).ifPresent(r -> out.put(r.id(), r));
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.FOOD_PROCESSOR.get();
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return NonNullList.of(ItemStack.EMPTY, result);
    }

    @Override
    public List<IngredientWithSize> getItemInputs() {
        return inputs;
    }

    @Override
    public List<SizedFluidIngredient> getFluidInputs() {
        return fluidInput == null ? List.of() : List.of(fluidInput);
    }

    public NonNullList<IngredientWithSize> getInputs() {
        return inputs;
    }

    public @Nullable SizedFluidIngredient getFluidInput() {
        return fluidInput;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    public boolean shouldCheckItemAvailability() {
        return false;
    }
}
