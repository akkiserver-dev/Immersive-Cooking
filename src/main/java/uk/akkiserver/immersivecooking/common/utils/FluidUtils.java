package uk.akkiserver.immersivecooking.common.utils;

import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.item.ItemStack;

public final class FluidUtils {
    public static boolean drainFluidContainer(IFluidHandler tank, int slotIn, int slotOut, IItemHandlerModifiable inv) {
        ItemStack inputStack = inv.getStackInSlot(slotIn);
        if (inputStack.isEmpty()) return false;

        ItemStack containerCopy = inputStack.copy();
        containerCopy.setCount(1);

        var result = FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, false);

        if (result.isSuccess()) {
            ItemStack emptyContainer = result.getResult();

            ItemStack outputStack = inv.getStackInSlot(slotOut);
            boolean canStack = outputStack.isEmpty() ||
                    (ItemHandlerHelper.canItemStacksStack(outputStack, emptyContainer) &&
                            outputStack.getCount() + emptyContainer.getCount() <= outputStack.getMaxStackSize());

            if (canStack) {
                FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, true);

                if (outputStack.isEmpty()) {
                    inv.setStackInSlot(slotOut, emptyContainer);
                } else {
                    inv.getStackInSlot(slotOut).grow(emptyContainer.getCount());
                }

                inv.getStackInSlot(slotIn).shrink(1);
                if (inv.getStackInSlot(slotIn).isEmpty()) {
                    inv.setStackInSlot(slotIn, ItemStack.EMPTY);
                }

                return true;
            }
        }

        return false;
    }
}
