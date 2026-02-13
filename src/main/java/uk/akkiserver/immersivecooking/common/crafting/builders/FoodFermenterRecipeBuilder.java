package uk.akkiserver.immersivecooking.common.crafting.builders;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import uk.akkiserver.immersivecooking.common.ICRecipes;

public class FoodFermenterRecipeBuilder extends IEFinishedRecipe<FoodFermenterRecipeBuilder> {
    private FoodFermenterRecipeBuilder() {
        super(ICRecipes.Serializers.FOOD_FERMENTER.get());
        this.setUseInputArray(6);
    }

    public static FoodFermenterRecipeBuilder builder(ItemStack result) {
        return new FoodFermenterRecipeBuilder().addResult(result);
    }

    public static FoodFermenterRecipeBuilder builder(ItemLike result) {
        return new FoodFermenterRecipeBuilder().addResult(result);
    }

    public FoodFermenterRecipeBuilder setFluidInput(FluidStack fluid) {
        return addFluid("fluid", fluid);
    }

    public FoodFermenterRecipeBuilder setFluidInput(TagKey<Fluid> fluid, int amount) {
        return addWriter(json -> {
            var jsonObject = new com.google.gson.JsonObject();
            jsonObject.addProperty("tag", fluid.location().toString());
            jsonObject.addProperty("amount", amount);
            json.add("fluid", jsonObject);
        });
    }

    public FoodFermenterRecipeBuilder setFluidInput(Fluid fluid, int amount) {
        return setFluidInput(new FluidStack(fluid, amount));
    }

    public FoodFermenterRecipeBuilder setContainer(TagKey<Item> container) {
        return addWriter(json -> json.addProperty("container", "#" + container.location()));
    }

    public FoodFermenterRecipeBuilder setContainer(ItemLike container) {
        return setContainer(new ItemStack(container));
    }

    public FoodFermenterRecipeBuilder setContainer(ItemStack container) {
        return addWriter(json -> json.add("container", serializeItemStack(container)));
    }
}
