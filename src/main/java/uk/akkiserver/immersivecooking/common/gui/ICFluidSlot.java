package uk.akkiserver.immersivecooking.common.gui;

import blusunrize.immersiveengineering.common.gui.IESlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import uk.akkiserver.immersivecooking.common.utils.FluidUtils;

public class ICFluidSlot extends IESlot.NewFluidContainer {
    private final Filter filter;

    public ICFluidSlot(IItemHandler inv, int id, int x, int y, Filter filter) {
        super(inv, id, x, y, filter);
        this.filter = filter;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack)
                || (filter != Filter.EMPTY
                && FluidUtils.isFluidRelatedItemStack(stack));
    }
}
