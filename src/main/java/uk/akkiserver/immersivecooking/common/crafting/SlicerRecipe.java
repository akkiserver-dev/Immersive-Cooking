package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.ApiUtils;
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
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.compat.RecipeConverter;

import java.util.*;

public class SlicerRecipe extends MultiblockRecipe {
    public static final Map<ResourceLocation, RecipeHolder<SlicerRecipe>> RECIPES = new LinkedHashMap<>();
    public static final List<RecipeConverter<?, SlicerRecipe>> RECIPE_CONVERTERS = new ArrayList<>();

    public final IngredientWithSize input;
    public final ItemStack result;
    public final List<StackWithChance> byproducts;

    public SlicerRecipe(IngredientWithSize input, ItemStack result, int time, int energy) {
        this(input, result, List.of(), time, energy);
    }

    public SlicerRecipe(IngredientWithSize input, ItemStack result, List<StackWithChance> byproducts, int time, int energy) {
        super(TagOutput.EMPTY, ICRecipes.Types.SLICER, time, energy, ICRecipes.NO_MULTIPLIER);
        this.input = input;
        this.result = result;
        this.byproducts = List.copyOf(byproducts);

        this.setInputListWithSizes(List.of(input));
        this.outputList = new TagOutputList(new TagOutput(result));
    }

    public static Optional<RecipeHolder<SlicerRecipe>> findRecipe(RecipeInput input, Level level) {
        return RECIPES.values().stream()
                .filter(h -> h.value().matches(input, level))
                .findFirst();
    }

    public static Optional<RecipeHolder<SlicerRecipe>> findRecipe(ItemStack input) {
        if (input.isEmpty()) return Optional.empty();
        return RECIPES.values().stream()
                .filter(h -> h.value().input.test(input))
                .findFirst();
    }

    public static Optional<RecipeHolder<SlicerRecipe>> findRecipeByOutput(ItemStack output) {
        return RECIPES.values().stream()
                .filter(h -> ItemStack.isSameItem(h.value().result, output)
                        || h.value().byproducts.stream().anyMatch(p -> ItemStack.isSameItem(p.stack().get(), output)))
                .findFirst();
    }

    public static void updateRecipes(RecipeManager recipeManager, HolderLookup.Provider provider, Map<ResourceLocation, RecipeHolder<SlicerRecipe>> recipes) {
        Map<ResourceLocation, RecipeHolder<SlicerRecipe>> newRecipes = new LinkedHashMap<>(new TreeMap<>(recipes));

        for (RecipeConverter<?, SlicerRecipe> converter : RECIPE_CONVERTERS) {
            ImmersiveCooking.LOGGER.info("[SlicerRecipe] Converting recipes from {}...", BuiltInRegistries.RECIPE_TYPE.getKey(converter.sourceType()));
            for (RecipeHolder<?> holder : recipeManager.getRecipes().stream().sorted(Comparator.comparing(RecipeHolder::id)).toList()) {
                tryConvert(converter, holder, recipeManager, provider, newRecipes);
            }
        }

        RECIPES.clear();
        RECIPES.putAll(newRecipes);
    }

    @SuppressWarnings("unchecked")
    private static <I extends Recipe<?>> void tryConvert(RecipeConverter<I, SlicerRecipe> converter, RecipeHolder<?> holder, RecipeManager recipeManager, HolderLookup.Provider provider, final Map<ResourceLocation, RecipeHolder<SlicerRecipe>> out) {
        if (holder.value().getType() != converter.sourceType()) return;

        converter.convert((RecipeHolder<I>) holder, recipeManager, provider).ifPresent(r -> out.putIfAbsent(r.id(), r));
    }

    @Override
    public boolean matches(RecipeInput inv, Level level) {
        return inv.size() == 1 && !inv.getItem(0).isEmpty() && input.test(inv.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput inv, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.SLICER.get();
    }

    public IngredientWithSize getInput() {
        return input;
    }

    public ItemStack getResult() {
        return result;
    }

    public List<StackWithChance> getByproducts() {
        return byproducts;
    }

    @Override
    public NonNullList<ItemStack> getActualItemOutputs() {
        NonNullList<ItemStack> outputs = NonNullList.create();
        outputs.add(result.copy());
        for (StackWithChance byproduct : byproducts) {
            ItemStack stack = byproduct.stack().get();
            if (!stack.isEmpty() && (byproduct.chance() >= 1 || ApiUtils.RANDOM.nextFloat() < byproduct.chance()))
                outputs.add(stack.copy());
        }
        return outputs;
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
