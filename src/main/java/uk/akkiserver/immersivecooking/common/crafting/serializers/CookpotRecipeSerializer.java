package uk.akkiserver.immersivecooking.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import uk.akkiserver.immersivecooking.api.codec.ICDualCodecs;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public class CookpotRecipeSerializer extends IERecipeSerializer<CookpotRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, CookpotRecipe> CODEC = DualCompositeMapCodecs.composite(
            ICDualCodecs.NONNULL_INGREDIENTS_SIZED.fieldOf("inputs"), CookpotRecipe::getInputs,
            DualCodecs.ITEM_STACK.fieldOf("result"), CookpotRecipe::getResult,
            DualCodecs.ITEM_STACK.fieldOf("container"), CookpotRecipe::getContainer,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            DualCodecs.INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            CookpotRecipe::new
    );

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, CookpotRecipe> codecs() {
        return CODEC;
    }

    @Override
    public ItemStack getIcon() {
        return ICContent.Multiblock.COOKPOT.iconStack();
    }
}
