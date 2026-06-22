package uk.akkiserver.immersivecooking.common.utils;

import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.minecraft.world.item.ItemStack;
import uk.akkiserver.immersivecooking.common.crafting.providers.fluid.IItemFluidRelationProvider;

import java.util.ArrayList;
import java.util.List;

public final class FluidUtils {
    private static final List<IItemFluidRelationProvider> FLUID_RELATION_PROVIDERS = new ArrayList<>();

    public static boolean isFluidRelatedItemStack(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()
                || FLUID_RELATION_PROVIDERS.stream().anyMatch(p -> p.canProvide() && p.isFluidContainer(stack));
    }

    public static void registerFluidRelationProvider(IItemFluidRelationProvider provider) {
        FLUID_RELATION_PROVIDERS.add(provider);
    }

    public static boolean drainFluidContainer(IFluidHandler tank, int slotIn, int slotOut, IItemHandlerModifiable inv) {
        ItemStack inputStack = inv.getStackInSlot(slotIn);
        if (inputStack.isEmpty()) return false;

        ItemStack containerCopy = inputStack.copy();
        containerCopy.setCount(1);

        var result = FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, false);
        if (result.isSuccess()) {
            ItemStack emptyContainer = result.getResult();
            if (canOutput(inv, slotOut, emptyContainer)) {
                FluidUtil.tryEmptyContainer(containerCopy, tank, Integer.MAX_VALUE, null, true);
                insertOutput(inv, slotOut, emptyContainer);
                inv.getStackInSlot(slotIn).shrink(1);
                if (inv.getStackInSlot(slotIn).isEmpty())
                    inv.setStackInSlot(slotIn, ItemStack.EMPTY);
                return true;
            }
        }

        for (IItemFluidRelationProvider provider : FLUID_RELATION_PROVIDERS) {
            if (!provider.canProvide() || !provider.isFluidContainer(containerCopy)) continue;

            FluidStack contained = provider.getContainedFluid(containerCopy);
            if (contained.isEmpty()) continue;

            int filled = tank.fill(contained, IFluidHandler.FluidAction.SIMULATE);
            if (filled < contained.getAmount()) continue;

            ItemStack emptyContainer = provider.getEmptyContainer(containerCopy);
            if (!canOutput(inv, slotOut, emptyContainer)) continue;

            tank.fill(contained, IFluidHandler.FluidAction.EXECUTE);
            insertOutput(inv, slotOut, emptyContainer);
            inv.getStackInSlot(slotIn).shrink(1);
            if (inv.getStackInSlot(slotIn).isEmpty())
                inv.setStackInSlot(slotIn, ItemStack.EMPTY);
            return true;
        }

        return false;
    }

    private static boolean canOutput(IItemHandlerModifiable inv, int slotOut, ItemStack stack) {
        if (stack.isEmpty()) return true;
        ItemStack outputStack = inv.getStackInSlot(slotOut);
        return outputStack.isEmpty() ||
                (ItemHandlerHelper.canItemStacksStack(outputStack, stack) &&
                        outputStack.getCount() + stack.getCount() <= outputStack.getMaxStackSize());
    }

    private static void insertOutput(IItemHandlerModifiable inv, int slotOut, ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemStack outputStack = inv.getStackInSlot(slotOut);
        if (outputStack.isEmpty()) {
            inv.setStackInSlot(slotOut, stack);
        } else {
            outputStack.grow(stack.getCount());
        }
    }
}