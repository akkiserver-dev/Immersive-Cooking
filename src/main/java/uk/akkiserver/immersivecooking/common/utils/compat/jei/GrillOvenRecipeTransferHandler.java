package uk.akkiserver.immersivecooking.common.utils.compat.jei;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.gui.GrillOvenMenu;

import java.util.Optional;

public class GrillOvenRecipeTransferHandler implements IRecipeTransferHandler<GrillOvenMenu, SmokingRecipe> {
    private final IRecipeTransferHandlerHelper helper;

    public GrillOvenRecipeTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    @Override
    public Class<GrillOvenMenu> getContainerClass() {
        return GrillOvenMenu.class;
    }

    @Override
    public Optional<MenuType<GrillOvenMenu>> getMenuType() {
        return Optional.of((MenuType<GrillOvenMenu>) ICContent.MenuTypes.GRILL_OVEN.getType());
    }

    @Override
    public RecipeType<SmokingRecipe> getRecipeType() {
        return RecipeTypes.SMOKING;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(GrillOvenMenu container, SmokingRecipe recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        java.util.List<Integer> inputSlots = java.util.List.of(0, 1, 2);
        IRecipeTransferError lasterror = null;
        boolean success = false;

        for (int slotIndex : inputSlots) {
            // Create a temporary handler for this specific slot.
            var handler = helper.createUnregisteredRecipeTransferHandler(
                    new mezz.jei.api.recipe.transfer.IRecipeTransferInfo<GrillOvenMenu, SmokingRecipe>() {
                        @Override
                        public Class<GrillOvenMenu> getContainerClass() {
                            return GrillOvenMenu.class;
                        }

                        @Override
                        public Optional<MenuType<GrillOvenMenu>> getMenuType() {
                            return Optional.of((MenuType<GrillOvenMenu>) ICContent.MenuTypes.GRILL_OVEN.getType());
                        }

                        @Override
                        public RecipeType<SmokingRecipe> getRecipeType() {
                            return RecipeTypes.SMOKING;
                        }

                        @Override
                        public boolean canHandle(GrillOvenMenu container, SmokingRecipe recipe) {
                            return true;
                        }

                        @Override
                        public java.util.List<net.minecraft.world.inventory.Slot> getRecipeSlots(
                                GrillOvenMenu container,
                                SmokingRecipe recipe) {
                            return java.util.Collections.singletonList(container.getSlot(slotIndex));
                        }

                        @Override
                        public java.util.List<net.minecraft.world.inventory.Slot> getInventorySlots(
                                GrillOvenMenu container,
                                SmokingRecipe recipe) {
                            java.util.List<net.minecraft.world.inventory.Slot> slots = new java.util.ArrayList<>();
                            for (int i = 4; i < container.slots.size(); i++) {
                                slots.add(container.getSlot(i));
                            }
                            return slots;
                        }
                    });

            IRecipeTransferError error = handler.transferRecipe(container, recipe, recipeSlots, player, maxTransfer,
                    doTransfer);
            if (error == null) {
                success = true;
                if (!maxTransfer) {
                    return null;
                }
            } else {
                lasterror = error;
            }
        }

        if (success)
            return null;
        return lasterror;
    }
}
