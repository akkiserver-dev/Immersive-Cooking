package uk.akkiserver.immersivecooking.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import uk.akkiserver.immersivecooking.api.codec.ICDualCodecs;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;

public class FoodProcessorRecipeSerializer extends IERecipeSerializer<FoodProcessorRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, FoodProcessorRecipe> CODEC = DualCompositeMapCodecs.composite(
            ICDualCodecs.NONNULL_INGREDIENTS_SIZED.fieldOf("inputs"), FoodProcessorRecipe::getInputs,
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("fluid"), FoodProcessorRecipe::getFluidInput,
            DualCodecs.ITEM_STACK.fieldOf("result"), FoodProcessorRecipe::getResult,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            DualCodecs.INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            FoodProcessorRecipe::new
    );

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, FoodProcessorRecipe> codecs() {
        return CODEC;
    }

    @Override
    public ItemStack getIcon() {
        return ICContent.Multiblock.FOOD_PROCESSOR.iconStack();
    }
}
