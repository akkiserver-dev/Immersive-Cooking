package uk.akkiserver.immersivecooking.common.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic;

import javax.annotation.Nonnull;

public class GrillOvenMenu extends ICContainerMenu {
    public final ContainerData data;
    private final RecipeManager.CachedCheck<Container, SmokingRecipe> smokingChecker = RecipeManager.createCheck(RecipeType.SMOKING);

    public static GrillOvenMenu makeServer(MenuType<?> type, int id, Inventory playerInventory, MultiblockMenuContext<GrillOvenLogic.State> ctx) {
        final GrillOvenLogic.State state = ctx.mbContext().getState();
        return new GrillOvenMenu(
                multiblockCtx(type, id, ctx), playerInventory, state.getInventory().getRawHandler(), state
        );
    }

    public static GrillOvenMenu makeClient(MenuType<?> type, int id, Inventory playerInventory) {
        return new GrillOvenMenu(
                clientCtx(type, id),
                playerInventory,
                new ItemStackHandler(GrillOvenLogic.NUM_SLOTS),
                new SimpleContainerData(GrillOvenLogic.DATA_SLOTS)
        );
    }

    private GrillOvenMenu(MenuContext ctx, Inventory inventoryPlayer, IItemHandler inventory, ContainerData containerData) {
        super(ctx);

        final Level level = inventoryPlayer.player.level();
        final GrillOvenLogic logic = (GrillOvenLogic) ICContent.Multiblock.GRILL_OVEN.logic();

        this.addSlot(new SlotItemHandler(inventory, GrillOvenLogic.IO_SLOT_0, 62, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return logic.findRecipe(itemStack, level).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(inventory, GrillOvenLogic.IO_SLOT_1, 80, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return logic.findRecipe(itemStack, level).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(inventory, GrillOvenLogic.IO_SLOT_2, 98, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return logic.findRecipe(itemStack, level).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(inventory, GrillOvenLogic.FUEL_SLOT, 80, 53) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return ForgeHooks.getBurnTime(itemStack, RecipeType.SMOKING) > 0;
            }
        });
        ownSlotCount = GrillOvenLogic.NUM_SLOTS;

        addInventorySlots(inventoryPlayer);

        this.data = containerData;
        addDataSlots(data);
    }
}
