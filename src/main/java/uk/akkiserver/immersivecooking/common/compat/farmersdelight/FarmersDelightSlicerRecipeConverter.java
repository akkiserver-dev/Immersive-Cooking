package uk.akkiserver.immersivecooking.common.compat.farmersdelight;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import uk.akkiserver.immersivecooking.common.compat.RecipeConverter;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;
import uk.akkiserver.immersivecooking.common.util.Resource;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.util.Optional;
import java.util.ArrayList;

public class FarmersDelightSlicerRecipeConverter implements RecipeConverter<CuttingBoardRecipe, SlicerRecipe> {
    private static final TagKey<Item> KNIVES = ItemTags.create(Resource.common("tools/knife"));
    private static final TagKey<Item> LEGACY_KNIVES = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "tools/knives"));

    @Override
    public RecipeType<CuttingBoardRecipe> sourceType() {
        return ModRecipeTypes.CUTTING.get();
    }

    @Override
    public Optional<RecipeHolder<SlicerRecipe>> convert(RecipeHolder<CuttingBoardRecipe> holder, RecipeManager manager, HolderLookup.Provider provider) {
        var source = holder.value();
        if (!acceptsKnife(source.getTool())) return Optional.empty();
        var results = source.getRollableResults();
        if (source.getIngredients().size() != 1) return Optional.empty();
        int primaryIndex = -1;
        for (int i = 0; i < results.size(); i++) {
            var result = results.get(i);
            if (result.chance() == 1 && result.stack().has(DataComponents.FOOD)) {
                primaryIndex = i;
                break;
            }
        }
        if (primaryIndex < 0) return Optional.empty();
        var byproducts = new ArrayList<StackWithChance>();
        for (int i = 0; i < results.size(); i++) {
            if (i == primaryIndex) continue;
            var result = results.get(i);
            byproducts.add(new StackWithChance(result.stack().copy(), result.chance()));
        }
        var recipe = new SlicerRecipe(
                new IngredientWithSize(source.getIngredients().getFirst()),
                results.get(primaryIndex).stack().copy(),
                byproducts,
                80,
                1600
        );
        return Optional.of(new RecipeHolder<>(Resource.wrapFor(holder.id()), recipe));
    }

    private static boolean acceptsKnife(Ingredient tool) {
        var custom = tool.getCustomIngredient();
        if (custom instanceof ItemAbilityIngredient ability)
            return KnifeItem.KNIFE_ACTIONS.contains(ability.getItemAbility());
        if (custom instanceof CompoundIngredient compound)
            return compound.children().stream().anyMatch(FarmersDelightSlicerRecipeConverter::acceptsKnife);
        if (custom != null)
            return custom.getItems().anyMatch(stack -> stack.canPerformAction(KnifeItem.KNIFE_DIG));
        for (var value : tool.getValues()) {
            // Recognize knife tags before tags are bound during recipe reload, without caching empty ingredients.
            if (value instanceof Ingredient.TagValue tag
                    && (tag.tag().equals(KNIVES) || tag.tag().equals(LEGACY_KNIVES))) return true;
            if (value.getItems().stream().anyMatch(stack -> stack.canPerformAction(KnifeItem.KNIFE_DIG)
                    || stack.is(KNIVES) || stack.is(LEGACY_KNIVES))) return true;
        }
        return false;
    }
}
