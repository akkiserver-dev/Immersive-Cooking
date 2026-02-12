package uk.akkiserver.immersivecookfarm.common.crafting.serializers;

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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.crafting.CookpotRecipe;

public class CookpotRecipeSerializer extends IERecipeSerializer<CookpotRecipe> {
    @Override
    public ItemStack getIcon() {
        return ICContent.Multiblock.COOKPOT.iconStack();
    }

    @Override
    public CookpotRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
        JsonArray ingredientArray = GsonHelper.getAsJsonArray(json, "ingredients");
        NonNullList<IngredientWithSize> inputs = NonNullList.create();
        for (JsonElement e : ingredientArray) {
            inputs.add(IngredientWithSize.deserialize(e));
        }
        ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
        ItemStack container = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "container"), true);
        int time = GsonHelper.getAsInt(json, "time", 200);
        int energy = GsonHelper.getAsInt(json, "energy", 2000);
        return new CookpotRecipe(recipeId, inputs, output, container, time, energy);
    }

    @Override
    public @Nullable CookpotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        NonNullList<IngredientWithSize> inputs = NonNullList.withSize(size, IngredientWithSize.of(ItemStack.EMPTY));
        for (int i = 0; i < size; i++) {
            inputs.set(i, IngredientWithSize.read(buffer));
        }
        ItemStack output = buffer.readItem();
        ItemStack container = buffer.readItem();
        int time = buffer.readVarInt();
        int energy = buffer.readVarInt();
        return new CookpotRecipe(recipeId, inputs, output, container, time, energy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CookpotRecipe recipe) {
        buffer.writeVarInt(recipe.inputs.size());
        for (IngredientWithSize i : recipe.inputs) {
            i.write(buffer);
        }
        buffer.writeItem(recipe.itemOutput);
        buffer.writeItem(recipe.container);
        buffer.writeVarInt(recipe.getTotalProcessTime());
        buffer.writeVarInt(recipe.getTotalProcessEnergy());
    }
}
