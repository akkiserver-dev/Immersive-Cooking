package uk.akkiserver.immersivecooking.common.gui;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.FoodProcessorLogic;

public class FoodProcessorMenu extends ICContainerMenu {
    public final EnergyStorage energyStorage;
    public final FluidTank tank;
    public final ContainerData data;

    private FoodProcessorMenu(MenuContext ctx, Inventory playerInventory, IItemHandler inventory, MutableEnergyStorage energyStorage, FluidTank tank, ContainerData data) {
        super(ctx);
        this.energyStorage = energyStorage;
        this.tank = tank;
        this.data = data;

        for (int i = 0; i < 6; i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 62 + (i % 3) * 18, 26 + (i / 3) * 18));
        }

        this.addSlot(new IESlot.NewOutput(inventory, FoodProcessorLogic.EMPTY_FLUID_SLOT, 38, 54));
        this.addSlot(new IESlot.NewFluidContainer(inventory, FoodProcessorLogic.FILLED_FLUID_SLOT, 38, 15, IESlot.NewFluidContainer.Filter.ANY) {
            @Override
            public boolean mayPickup(Player playerIn) {
                return true;
            }
        });

        this.addSlot(new IESlot.NewOutput(inventory, FoodProcessorLogic.OUTPUT_SLOT, 133, 54));

        this.ownSlotCount = FoodProcessorLogic.NUM_SLOTS;

        addInventorySlots(playerInventory);
        addGenericData(GenericContainerData.energy(energyStorage));
        addGenericData(GenericContainerData.fluid(tank));
        addDataSlots(data);
    }

    public static FoodProcessorMenu makeServer(MenuType<?> type, int id, Inventory playerInventory,
                                               MultiblockMenuContext<FoodProcessorLogic.State> ctx) {
        final FoodProcessorLogic.State state = ctx.mbContext().getState();
        return new FoodProcessorMenu(
                multiblockCtx(type, id, ctx),
                playerInventory,
                state.getInventory(),
                state.getEnergy(),
                state.getTank(),
                state);
    }

    public static FoodProcessorMenu makeClient(MenuType<?> type, int id, Inventory playerInventory) {
        return new FoodProcessorMenu(
                clientCtx(type, id),
                playerInventory,
                new ItemStackHandler(FoodProcessorLogic.NUM_SLOTS),
                new AveragingEnergyStorage(FoodProcessorLogic.ENERGY_CAPACITY),
                new FluidTank(12000),
                new SimpleContainerData(2));
    }
}
