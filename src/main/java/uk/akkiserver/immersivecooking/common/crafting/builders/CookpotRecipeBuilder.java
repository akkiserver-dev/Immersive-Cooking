package uk.akkiserver.immersivecooking.common.crafting.builders;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import uk.akkiserver.immersivecooking.common.ICRecipes;

public class CookpotRecipeBuilder extends IEFinishedRecipe<CookpotRecipeBuilder> {
    private CookpotRecipeBuilder() {
        super(ICRecipes.Serializers.COOKPOT.get());
        this.setUseInputArray(6);
    }

    public static CookpotRecipeBuilder builder(ItemStack result) {
        return new CookpotRecipeBuilder().addResult(result);
    }

    public static CookpotRecipeBuilder builder(ItemLike result) {
        return new CookpotRecipeBuilder().addResult(result);
    }

    public CookpotRecipeBuilder setContainer(TagKey<Item> container) {
        return addWriter(json -> json.addProperty("container", "#" + container.location()));
    }

    public CookpotRecipeBuilder setContainer(ItemLike container) {
        return setContainer(new ItemStack(container));
    }

    public CookpotRecipeBuilder setContainer(ItemStack container) {
        return addWriter(json -> json.add("container", serializeItemStack(container)));
    }
}
