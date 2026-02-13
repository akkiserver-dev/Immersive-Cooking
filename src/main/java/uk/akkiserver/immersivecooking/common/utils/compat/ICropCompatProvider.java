package uk.akkiserver.immersivecooking.common.utils.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface ICropCompatProvider {
    ItemStack getSeed();

    ItemStack getCrop();

    Block getBlock();

    NonNullList<ItemStack> getByProducts();

    int getMaxDrop();

    String getCropId();

    ResourceLocation getCropLoc();

    String getModId();
}
