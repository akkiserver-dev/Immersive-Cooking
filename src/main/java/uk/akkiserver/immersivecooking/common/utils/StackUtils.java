package uk.akkiserver.immersivecooking.common.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class StackUtils {
    public static ItemStack getSafeItemStack(ResourceLocation location, int amount) {
        return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(location)), amount);
    }

    public static ItemStack getSafeItemStack(ResourceLocation location) {
        return getSafeItemStack(location, 1);
    }

    public static ItemStack getItemStackOrElse(ResourceLocation location, ItemStack fallback, int amount) {
        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item != null ? new ItemStack(item, amount) : fallback;
    }

    /**
     * @param fallback fallback item id must be existing item
     */
    public static ItemStack getItemStackOrElse(ResourceLocation location, ResourceLocation fallback, int amount) {
        return getItemStackOrElse(location, getSafeItemStack(fallback), amount);
    }

    public static ItemStack getItemStackOrElse(ResourceLocation location, ItemStack fallback) {
        return getItemStackOrElse(location, fallback, 1);
    }

    public static ItemStack getItemStackOrElse(ResourceLocation location, ResourceLocation fallback) {
        return getItemStackOrElse(location, fallback, 1);
    }

    /**
     * Fallback to AIR
     */
    public static ItemStack getItemStack(ResourceLocation location, int amount) {
        return getItemStackOrElse(location, ItemStack.EMPTY, amount);
    }

    /**
     * Fallback to AIR
     */
    public static ItemStack getItemStack(ResourceLocation location) {
        return getItemStackOrElse(location, ItemStack.EMPTY, 1);
    }
}
