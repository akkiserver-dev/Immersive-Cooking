package uk.akkiserver.immersivecooking.common.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Objects;
import java.util.Optional;

public final class InventoryUtils {
    /**
     * Returns an ItemStack for the specified item ID.
     *
     * @param location the item ID
     * @param amount the stack size
     * @return a new ItemStack
     * @throws NullPointerException if the item ID is not registered
     */
    public static ItemStack getItemStackOrThrow(ResourceLocation location, int amount) {
        return new ItemStack(Objects.requireNonNull(BuiltInRegistries.ITEM.get(location)), amount);
    }

    /**
     * Returns an ItemStack for the specified item ID with a stack size of 1.
     *
     * @param location the item ID
     * @return a new ItemStack
     * @throws NullPointerException if the item ID is not registered
     */
    public static ItemStack getItemStackOrThrow(ResourceLocation location) {
        return getItemStackOrThrow(location, 1);
    }

    /**
     * Returns an ItemStack for the given item ID, or the specified fallback stack if not found.
     *
     * @param location the item ID to look up
     * @param fallback the stack to return if the item cannot be found
     * @param amount the stack size of the created ItemStack
     * @return a new ItemStack for the specified item ID, or {@code fallback} if not found
     */
    public static ItemStack getItemStackOrElse(ResourceLocation location, ItemStack fallback, int amount) {
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(location);
        return item.map(value -> new ItemStack(value, amount)).orElse(fallback.copy());
    }

    /**
     * Returns an ItemStack for the given item ID, or the specified fallback item if not found.
     *
     * @param location the item ID to look up
     * @param fallback the fallback item ID; must refer to a valid item
     * @param amount the stack size
     */
    public static ItemStack getItemStackOrElse(ResourceLocation location, ResourceLocation fallback, int amount) {
        return getItemStackOrElse(location, getItemStackOrThrow(fallback), amount);
    }

    /**
     * Returns an ItemStack for the given item ID, or the specified fallback item if not found.
     *
     * @param location the item ID to look up
     * @param fallback the fallback item ID; must refer to a valid item
     */
    public static ItemStack getItemStackOrElse(ResourceLocation location, ItemStack fallback) {
        return getItemStackOrElse(location, fallback, 1);
    }

    /**
     * Returns an ItemStack for the given item ID, or the specified fallback item if not found.
     *
     * @param location the item ID to look up
     * @param fallback the fallback item ID
     */
    public static ItemStack getItemStackOrElse(ResourceLocation location, ResourceLocation fallback) {
        return getItemStackOrElse(location, fallback, 1);
    }

    /**
     * Returns an ItemStack for the given item ID, or {@link ItemStack#EMPTY} if not found.
     *
     * @param amount the stack size
     */
    public static ItemStack getItemStack(ResourceLocation location, int amount) {
        return getItemStackOrElse(location, ItemStack.EMPTY, amount);
    }

    /**
     * Returns an ItemStack for the given item ID, or {@link ItemStack#EMPTY} if not found.
     */
    public static ItemStack getItemStack(ResourceLocation location) {
        return getItemStackOrElse(location, ItemStack.EMPTY, 1);
    }

    /**
     * Returns whether the specified stack can be inserted into the output slot.
     *
     * @param inv the item inventory
     * @param slotOut the output slot
     * @param stack the stack to insert
     * @return {@code true} if the stack can be inserted
     */
    public static boolean canOutput(IItemHandlerModifiable inv, int slotOut, ItemStack stack) {
        if (stack.isEmpty()) return true;
        ItemStack outputStack = inv.getStackInSlot(slotOut);
        return outputStack.isEmpty() ||
                (ItemStack.isSameItemSameComponents(outputStack, stack) &&
                        outputStack.getCount() + stack.getCount() <= outputStack.getMaxStackSize());
    }

    /**
     * Inserts the specified stack into the output slot.
     *
     * @param inv the item inventory
     * @param slotOut the output slot
     * @param stack the stack to insert
     */
    public static void insertOutput(IItemHandlerModifiable inv, int slotOut, ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemStack outputStack = inv.getStackInSlot(slotOut);
        if (outputStack.isEmpty()) {
            inv.setStackInSlot(slotOut, stack);
        } else {
            outputStack.grow(stack.getCount());
        }
    }
}
