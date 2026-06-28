package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.compat.RecipeConverter;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import java.util.*;

public class FoodFermenterRecipe extends MultiblockRecipe {
    public static final Map<ResourceLocation, RecipeHolder<FoodFermenterRecipe>> RECIPES = new HashMap<>();
    public static final List<RecipeConverter<?, FoodFermenterRecipe>> RECIPE_CONVERTERS = new ArrayList<>();

    public NonNullList<IngredientWithSize> inputs; // 6 slots
    @Nullable
    public final SizedFluidIngredient fluidInput;
    public final ItemStack container;
    public final ItemStack result;

    public FoodFermenterRecipe(NonNullList<IngredientWithSize> inputs,
                               @Nullable SizedFluidIngredient fluidInput,
                               ItemStack result, ItemStack container, int time, int energy) {
        super(
                TagOutput.EMPTY,
                ICRecipes.Types.FOOD_FERMENTER,
                time,
                energy,
                ICRecipes.NO_MULTIPLIER
        );
        this.fluidInput = fluidInput;
        this.container = container;
        this.result = result;
        this.inputs = inputs;

        this.setInputListWithSizes(new ArrayList<>(this.inputs));
        this.outputList = new TagOutputList(new TagOutput(result));
    }

    public static Optional<RecipeHolder<FoodFermenterRecipe>> findRecipe(RecipeInput input, Level level) {
        return RECIPES.values().stream()
                .filter(h -> h.value().fluidInput == null)
                .filter(h -> h.value().matches(input, level))
                .findFirst();
    }

    public static Optional<RecipeHolder<FoodFermenterRecipe>> findRecipe(RecipeInput input, FluidStack fluid, Level level) {
        return RECIPES.values().stream()
                .filter(h -> h.value().fluidInput != null)
                .filter(h -> h.value().matches(input, level))
                .filter(h -> {
                    SizedFluidIngredient fi = h.value().fluidInput;
                    return fi.ingredient().test(fluid) && fluid.getAmount() >= fi.amount();
                })
                .findFirst();
    }

    public static Optional<RecipeHolder<FoodFermenterRecipe>> findRecipeByOutput(ItemStack output) {
        return RECIPES.values().stream()
                .filter(h -> ItemStack.isSameItem(h.value().result, output))
                .findFirst();
    }

    public static Optional<RecipeHolder<FoodFermenterRecipe>> findRecipeByContainer(ItemStack container, FluidStack fluid) {
        return RECIPES.values().stream()
                .filter(h -> ItemStack.isSameItem(h.value().container, container))
                .filter(h -> h.value().fluidInput != null)
                .filter(h -> h.value().fluidInput.ingredient().test(fluid))
                .findFirst();
    }

    public static void updateRecipes(RecipeManager recipeManager, HolderLookup.Provider provider, Map<ResourceLocation, RecipeHolder<FoodFermenterRecipe>> recipes) {
        Map<ResourceLocation, RecipeHolder<FoodFermenterRecipe>> newRecipes = new HashMap<>(recipes);

        for (RecipeConverter<?, FoodFermenterRecipe> converter : RECIPE_CONVERTERS) {
            ImmersiveCooking.LOGGER.info("[FoodFermenterRecipe] Converting recipes from {}...", BuiltInRegistries.RECIPE_TYPE.getKey(converter.sourceType()));
            for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
                tryConvert(converter, holder, recipeManager, provider, newRecipes);
            }
        }

        RECIPES.clear();
        RECIPES.putAll(newRecipes);
    }

    @SuppressWarnings("unchecked")
    private static <I extends Recipe<?>> void tryConvert(
            RecipeConverter<I, FoodFermenterRecipe> converter,
            RecipeHolder<?> holder,
            RecipeManager recipeManager,
            HolderLookup.Provider provider,
            Map<ResourceLocation, RecipeHolder<FoodFermenterRecipe>> out
    ) {
        if (holder.value().getType() != converter.sourceType()) return;

        converter.convert((RecipeHolder<I>) holder, recipeManager, provider).ifPresent(r -> out.put(r.id(), r));
    }

    @Override
    public boolean matches(RecipeInput inv, Level level) {
        List<ItemStack> inventoryCopy = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
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

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.FOOD_FERMENTER.get();
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return NonNullList.of(ItemStack.EMPTY, result);
    }

    @Override
    public List<IngredientWithSize> getItemInputs() {
        return inputs;
    }

    public NonNullList<IngredientWithSize> getInputs() {
        return inputs;
    }

    @Override
    public List<SizedFluidIngredient> getFluidInputs() {
        return fluidInput == null ? List.of() : List.of(fluidInput);
    }

    public @Nullable SizedFluidIngredient getFluidInput() {
        return fluidInput;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int getMultipleProcessTicks() {
        return -1;
    }

    @Override
    public boolean shouldCheckItemAvailability() {
        return false;
    }
}
