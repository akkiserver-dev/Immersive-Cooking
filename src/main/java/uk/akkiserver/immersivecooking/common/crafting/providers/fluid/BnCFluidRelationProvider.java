package uk.akkiserver.immersivecooking.common.crafting.providers.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import uk.akkiserver.immersivecooking.common.utils.Compat;
import uk.akkiserver.immersivecooking.common.utils.compat.bnc.BnCDrinks;
import umpaz.brewinandchewin.common.registry.BnCItems;

public class BnCFluidRelationProvider implements IItemFluidRelationProvider {
    @Override
    public boolean canProvide() {
        return Compat.isBnCInstalled();
    }

    @Override
    public boolean isFluidContainer(ItemStack stack) {
        return BnCDrinks.fromItem(stack.getItem()).isPresent();
    }

    @Override
    public FluidStack getContainedFluid(ItemStack stack) {
        return BnCDrinks.fromItem(stack.getItem())
                .map(BnCDrinks::toFluidStack)
                .orElse(FluidStack.EMPTY);
    }

    @Override
    public ItemStack getEmptyContainer(ItemStack filledStack) {
        return BnCDrinks.fromItem(filledStack.getItem())
                .map(e -> new ItemStack(BnCItems.TANKARD.get()))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getFilledContainer(FluidStack fluid) {
        return BnCDrinks.fromFluid(fluid.getFluid())
                .map(e -> new ItemStack(e.getItem()))
                .orElse(ItemStack.EMPTY);
    }
}