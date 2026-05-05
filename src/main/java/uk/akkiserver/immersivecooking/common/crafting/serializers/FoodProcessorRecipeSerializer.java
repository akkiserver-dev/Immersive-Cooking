package uk.akkiserver.immersivecooking.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;

public class FoodProcessorRecipeSerializer extends IERecipeSerializer<FoodProcessorRecipe> {
    @Override
    public ItemStack getIcon() {
        return ICContent.Multiblock.FOOD_PROCESSOR.iconStack();
    }

    @Override
    public FoodProcessorRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
        JsonArray ingredientArray = GsonHelper.getAsJsonArray(json, "inputs");
        NonNullList<IngredientWithSize> inputs = NonNullList.create();
        for (JsonElement e : ingredientArray) {
            inputs.add(IngredientWithSize.deserialize(e));
        }

        FluidTagInput fluidInput = null;
        if (json.has("fluid")) {
            fluidInput = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "fluid"));
        }

        ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);

        int time = GsonHelper.getAsInt(json, "time", 200);
        int energy = GsonHelper.getAsInt(json, "energy", 2000);

        return new FoodProcessorRecipe(recipeId, inputs, fluidInput, output, time, energy);
    }

    @Override
    public @Nullable FoodProcessorRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        NonNullList<IngredientWithSize> inputs = NonNullList.withSize(size, IngredientWithSize.of(ItemStack.EMPTY));
        for (int i = 0; i < size; i++) {
            inputs.set(i, IngredientWithSize.read(buffer));
        }

        FluidTagInput fluidInput = FluidTagInput.read(buffer);
        ItemStack output = buffer.readItem();

        int time = buffer.readVarInt();
        int energy = buffer.readVarInt();

        return new FoodProcessorRecipe(recipeId, inputs, fluidInput, output, time, energy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FoodProcessorRecipe recipe) {
        buffer.writeVarInt(recipe.inputs.size());
        for (IngredientWithSize i : recipe.inputs) {
            i.write(buffer);
        }

        if (recipe.fluidInput != null) {
            recipe.fluidInput.write(buffer);
        }
        buffer.writeItem(recipe.result);

        buffer.writeVarInt(recipe.getTotalProcessTime());
        buffer.writeVarInt(recipe.getTotalProcessEnergy());
    }
}
