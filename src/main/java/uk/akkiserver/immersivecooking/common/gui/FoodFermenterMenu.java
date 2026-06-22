package uk.akkiserver.immersivecooking.common.gui;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.IESlot.NewFluidContainer.Filter;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.FoodFermenterLogic;

public class FoodFermenterMenu extends ICContainerMenu {
    public final EnergyStorage energyStorage;
    public final FluidTank tank;
    public final ContainerData data;

    private FoodFermenterMenu(MenuContext ctx, Inventory playerInventory, IItemHandler inventory, MutableEnergyStorage energyStorage, FluidTank tank, ContainerData data) {
        super(ctx);
        this.energyStorage = energyStorage;
        this.tank = tank;
        this.data = data;

        for (int i = 0; i < 6; i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 62 + (i % 3) * 18, 26 + (i / 3) * 18));
        }

        this.addSlot(new IESlot.NewOutput(inventory, FoodFermenterLogic.EMPTY_FLUID_SLOT, 38, 54));
        this.addSlot(new ICFluidSlot(inventory, FoodFermenterLogic.FILLED_FLUID_SLOT, 38, 15, Filter.ANY) {
            @Override
            public boolean mayPickup(Player playerIn) {
                return true;
            }
        });

        this.addSlot(new SlotItemHandler(inventory, FoodFermenterLogic.INPUT_CONTAINER_SLOT, 133, 15));
        this.addSlot(new IESlot.NewOutput(inventory, FoodFermenterLogic.OUTPUT_SLOT, 133, 54));

        this.ownSlotCount = FoodFermenterLogic.NUM_SLOTS;

        addInventorySlots(playerInventory);
        addGenericData(GenericContainerData.energy(energyStorage));
        addGenericData(GenericContainerData.fluid(tank));
        addDataSlots(data);
    }

    public static FoodFermenterMenu makeServer(MenuType<?> type, int id, Inventory playerInventory,
                                               MultiblockMenuContext<FoodFermenterLogic.State> ctx) {
        final FoodFermenterLogic.State state = ctx.mbContext().getState();
        return new FoodFermenterMenu(
                multiblockCtx(type, id, ctx),
                playerInventory,
                state.getInventory(),
                state.getEnergy(),
                state.getTank(),
                state);
    }

    public static FoodFermenterMenu makeClient(MenuType<?> type, int id, Inventory playerInventory) {
        return new FoodFermenterMenu(
                clientCtx(type, id),
                playerInventory,
                new ItemStackHandler(FoodFermenterLogic.NUM_SLOTS),
                new AveragingEnergyStorage(FoodFermenterLogic.ENERGY_CAPACITY),
                new FluidTank(12000),
                new SimpleContainerData(2));
    }
}
