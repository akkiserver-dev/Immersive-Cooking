package uk.akkiserver.immersivecooking.common.compat.bnc;

import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import uk.akkiserver.immersivecooking.api.fluids.IItemFluidRelationProvider;
import umpaz.brewinandchewin.common.registry.BnCItems;

public class BnCFluidRelationProvider implements IItemFluidRelationProvider {
    @Override
    public boolean canProvide() {
        return ModList.get().isLoaded("brewinandchewin");
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
                .map(e -> new ItemStack(BnCItems.TANKARD))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getFilledContainer(FluidStack fluid) {
        return BnCDrinks.fromFluid(fluid.getFluid())
                .map(e -> new ItemStack(e.getItem()))
                .orElse(ItemStack.EMPTY);
    }
}