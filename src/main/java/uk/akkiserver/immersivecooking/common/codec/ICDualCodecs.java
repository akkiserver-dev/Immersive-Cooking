package uk.akkiserver.immersivecooking.common.codec;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import malte0811.dualcodecs.DualCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;

public final class ICDualCodecs {
    public static final DualCodec<RegistryFriendlyByteBuf, NonNullList<IngredientWithSize>> NONNULL_INGREDIENTS_SIZED = new DualCodec<>(
            ICCodecs.NONNULL_INGREDIENTS_SIZED, ICCodecs.STREAM_NONNULL_INGREDIENTS_SIZED
    );
}
