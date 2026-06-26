package uk.akkiserver.immersivecooking.common.util;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;
import uk.akkiserver.immersivecooking.common.fluid.ItemFluidRelationProvider;

import java.util.ArrayList;
import java.util.List;

public final class FluidUtils {
    private static final List<ItemFluidRelationProvider> FLUID_RELATION_PROVIDERS = new ArrayList<>();

    /**
     * Returns whether the given item stack is related to a fluid.
     *
     * @param stack the item stack to check
     * @return {@code true} if the stack is a fluid container or is associated with a fluid
     */
    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null
                || FLUID_RELATION_PROVIDERS.stream().anyMatch(p -> p.isFluidContainer(stack));
    }

    public static void registerFluidRelationProvider(ItemFluidRelationProvider provider) {
        FLUID_RELATION_PROVIDERS.add(provider);
    }

    /**
     * Attempts to drain a fluid container from the input slot into the specified tank.
     *
     * @param tank the target fluid tank
     * @param slotIn the input slot containing the fluid container
     * @param slotOut the output slot for the emptied container
     * @param inv the item inventory
     * @return {@code true} if fluid was transferred, otherwise {@code false}
     */
    public static boolean drainFluidContainer(IFluidHandler tank, int slotIn, int slotOut, IItemHandlerModifiable inv) {
        ItemStack inputStack = inv.getStackInSlot(slotIn);
        if (inputStack.isEmpty()) return false;

        ItemStack containerCopy = inputStack.copy();
        containerCopy.setCount(1);

        var result = FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, false);
        if (result.isSuccess()) {
            ItemStack emptyContainer = result.getResult();
            if (InventoryUtils.canOutput(inv, slotOut, emptyContainer)) {
                FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, true);
                InventoryUtils.insertOutput(inv, slotOut, emptyContainer);
                inv.getStackInSlot(slotIn).shrink(1);
                if (inv.getStackInSlot(slotIn).isEmpty())
                    inv.setStackInSlot(slotIn, ItemStack.EMPTY);
                return true;
            }
        }

        for (ItemFluidRelationProvider provider : FLUID_RELATION_PROVIDERS) {
            if (!provider.isFluidContainer(containerCopy)) continue;

            FluidStack contained = provider.getContainedFluid(containerCopy);
            if (contained.isEmpty()) continue;

            int filled = tank.fill(contained, IFluidHandler.FluidAction.SIMULATE);
            if (filled < contained.getAmount()) continue;

            ItemStack emptyContainer = provider.getEmptyContainer(containerCopy);
            if (!InventoryUtils.canOutput(inv, slotOut, emptyContainer)) continue;

            tank.fill(contained, IFluidHandler.FluidAction.EXECUTE);
            InventoryUtils.insertOutput(inv, slotOut, emptyContainer);
            inv.getStackInSlot(slotIn).shrink(1);
            if (inv.getStackInSlot(slotIn).isEmpty())
                inv.setStackInSlot(slotIn, ItemStack.EMPTY);
            return true;
        }

        return false;
    }
}