package uk.akkiserver.immersivecooking.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import uk.akkiserver.immersivecooking.common.codec.ICDualCodecs;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;

public class FoodFermenterRecipeSerializer extends IERecipeSerializer<FoodFermenterRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, FoodFermenterRecipe> CODEC = DualCompositeMapCodecs.composite(
            ICDualCodecs.NONNULL_INGREDIENTS_SIZED.fieldOf("inputs"), FoodFermenterRecipe::getInputs,
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("fluid"), FoodFermenterRecipe::getFluidInput,
            DualCodecs.ITEM_STACK.fieldOf("result"), FoodFermenterRecipe::getResult,
            DualCodecs.ITEM_STACK.fieldOf("container"), FoodFermenterRecipe::getContainer,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            DualCodecs.INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            FoodFermenterRecipe::new
    );

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, FoodFermenterRecipe> codecs() {
        return CODEC;
    }

    @Override
    public ItemStack getIcon() {
        return ICContent.Multiblock.FOOD_FERMENTER.iconStack();
    }
}