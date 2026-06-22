package uk.akkiserver.immersivecooking.api.codec;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public final class ICCodecs {

    public static final Codec<NonNullList<IngredientWithSize>> NONNULL_INGREDIENTS_SIZED = NonNullList.codecOf(IngredientWithSize.CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<IngredientWithSize>> STREAM_NONNULL_INGREDIENTS_SIZED = IngredientWithSize.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(list -> {
                NonNullList<IngredientWithSize> nnl = NonNullList.create();
                nnl.addAll(list);
                return nnl;
                }, Function.identity());
}