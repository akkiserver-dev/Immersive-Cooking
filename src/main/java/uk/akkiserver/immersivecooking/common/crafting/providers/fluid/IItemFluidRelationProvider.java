package uk.akkiserver.immersivecooking.common.crafting.providers.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public interface IItemFluidRelationProvider {
    boolean canProvide();
    boolean isFluidContainer(ItemStack stack);
    FluidStack getContainedFluid(ItemStack stack);
    ItemStack getFilledContainer(FluidStack fluid);
    ItemStack getEmptyContainer(ItemStack filledStack);
}