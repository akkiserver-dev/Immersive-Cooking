package uk.akkiserver.immersivecooking.common.fluid;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface ItemFluidRelationProvider {
    boolean isFluidContainer(ItemStack stack);
    FluidStack getContainedFluid(ItemStack stack);
    ItemStack getFilledContainer(FluidStack fluid);
    ItemStack getEmptyContainer(ItemStack filledStack);
}