package uk.akkiserver.immersivecooking.api.fluids;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IItemFluidRelationProvider {
    boolean canProvide();
    boolean isFluidContainer(ItemStack stack);
    FluidStack getContainedFluid(ItemStack stack);
    ItemStack getFilledContainer(FluidStack fluid);
    ItemStack getEmptyContainer(ItemStack filledStack);
}