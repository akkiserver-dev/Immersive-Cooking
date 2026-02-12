package uk.akkiserver.immersivecooking.common.gui;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.CookpotLogic;

import javax.annotation.Nonnull;

public class CookpotMenu extends ICContainerMenu {
    public final EnergyStorage energyStorage;
    public final ContainerData data;

    public static CookpotMenu makeServer(MenuType<?> type, int id, Inventory playerInventory,
            MultiblockMenuContext<CookpotLogic.State> ctx) {
        final CookpotLogic.State state = ctx.mbContext().getState();
        return new CookpotMenu(
                multiblockCtx(type, id, ctx),
                playerInventory,
                state.getInventory(),
                state.getEnergy(),
                state);
    }

    public static CookpotMenu makeClient(MenuType<?> type, int id, Inventory playerInventory) {
        return new CookpotMenu(
                clientCtx(type, id),
                playerInventory,
                new ItemStackHandler(CookpotLogic.NUM_SLOTS),
                new AveragingEnergyStorage(CookpotLogic.ENERGY_CAPACITY),
                new SimpleContainerData(2));
    }

    private CookpotMenu(MenuContext ctx, Inventory playerInventory, IItemHandler inventory,
            MutableEnergyStorage energyStorage, ContainerData data) {
        super(ctx);
        this.energyStorage = energyStorage;
        this.data = data;

        for (int i = 0; i < 6; i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 23 + (i % 3) * 18, 19 + (i / 3) * 18));
        }

        this.addSlot(new SlotItemHandler(inventory, CookpotLogic.BOWL_SLOT, 91, 53));

        this.addSlot(new SlotItemHandler(inventory, CookpotLogic.OUTPUT_SLOT, 113, 53) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(inventory, CookpotLogic.OUTPUT_RAW_SLOT, 113, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return false;
            }
        });

        ownSlotCount = CookpotLogic.NUM_SLOTS;

        addInventorySlots(playerInventory);
        addGenericData(GenericContainerData.energy(energyStorage));
        addDataSlots(data);
    }
}
