package uk.akkiserver.immersivecooking.common.gui;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.FoodFermenterLogic;

public class FoodFermenterMenu extends ICContainerMenu {
    public final EnergyStorage energyStorage;
    public final FluidTank tank;

    private FoodFermenterMenu(MenuContext ctx, Inventory playerInventory, IItemHandler inventory, MutableEnergyStorage energyStorage, FluidTank tank) {
        super(ctx);
        this.energyStorage = energyStorage;
        this.tank = tank;

        for (int i = 0; i < 6; i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 23 + (i % 3) * 18, 19 + (i / 3) * 18));
        }


        addInventorySlots(playerInventory);
        addGenericData(GenericContainerData.energy(energyStorage));
        addGenericData(GenericContainerData.fluid(tank));
    }

    public static FoodFermenterMenu makeServer(MenuType<?> type, int id, Inventory playerInventory,
                                               MultiblockMenuContext<FoodFermenterLogic.State> ctx) {
        final FoodFermenterLogic.State state = ctx.mbContext().getState();
        return new FoodFermenterMenu(
                multiblockCtx(type, id, ctx),
                playerInventory,
                state.getInventory(),
                state.getEnergy(),
                state.getTank());
    }

    public static FoodFermenterMenu makeClient(MenuType<?> type, int id, Inventory playerInventory) {
        return new FoodFermenterMenu(
                clientCtx(type, id),
                playerInventory,
                new ItemStackHandler(FoodFermenterLogic.NUM_SLOTS),
                new AveragingEnergyStorage(FoodFermenterLogic.ENERGY_CAPACITY),
                new FluidTank(12000));
    }
}
