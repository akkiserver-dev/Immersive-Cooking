package uk.akkiserver.immersivecooking.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import malte0811.dualcodecs.DualCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.crafting.SlicerRecipe;

import java.util.List;

public class SlicerRecipeSerializer extends IERecipeSerializer<SlicerRecipe> {
    private static final DualCodec<RegistryFriendlyByteBuf, Integer> POSITIVE_INT = new DualCodec<>(
            Codec.intRange(1, Integer.MAX_VALUE), ByteBufCodecs.INT.cast());
    private static final DualCodec<RegistryFriendlyByteBuf, IngredientWithSize> INPUT = new DualCodec<>(
            IngredientWithSize.CODEC.validate(input -> input.getCount() > 0
                    ? DataResult.success(input) : DataResult.error(() -> "Slicer input count must be positive")),
            IngredientWithSize.STREAM_CODEC);
    private static final DualCodec<RegistryFriendlyByteBuf, List<StackWithChance>> BYPRODUCTS = new DualCodec<>(
            CHANCE_LIST_CODEC.validate(outputs -> outputs.stream().allMatch(output ->
                    Float.isFinite(output.chance()) && output.chance() >= 0 && output.chance() <= 1)
                    ? DataResult.success(outputs) : DataResult.error(() -> "Byproduct chance must be between 0 and 1")),
            StackWithChance.STREAM_LIST);

    public static final DualMapCodec<RegistryFriendlyByteBuf, SlicerRecipe> CODEC = DualCompositeMapCodecs.composite(
            INPUT.fieldOf("input"), SlicerRecipe::getInput,
            DualCodecs.ITEM_STACK.fieldOf("result"), SlicerRecipe::getResult,
            BYPRODUCTS.optionalFieldOf("byproducts", List.of()), SlicerRecipe::getByproducts,
            POSITIVE_INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            POSITIVE_INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            SlicerRecipe::new
    );

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, SlicerRecipe> codecs() {
        return CODEC;
    }

    @Override
    public ItemStack getIcon() {
        return ICContent.Multiblock.SLICER.iconStack();
    }
}
