package uk.akkiserver.immersivecooking.common.compat.jei;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.gui.GrillOvenMenu;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class GrillOvenRecipeTransferHandler implements IRecipeTransferHandler<GrillOvenMenu, RecipeHolder<SmokingRecipe>> {
    private final IRecipeTransferHandlerHelper helper;

    public GrillOvenRecipeTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    @Override
    public @NotNull Class<GrillOvenMenu> getContainerClass() {
        return GrillOvenMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<GrillOvenMenu>> getMenuType() {
        return Optional.of(ICContent.MenuTypes.GRILL_OVEN.getType());
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<SmokingRecipe>> getRecipeType() {
        return RecipeTypes.SMOKING;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(GrillOvenMenu container, RecipeHolder<SmokingRecipe> recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        List<Integer> inputSlots = List.of(0, 1, 2);
        IRecipeTransferError lasterror = null;
        boolean success = false;

        for (int slotIndex : inputSlots) {
            var handler = helper.createUnregisteredRecipeTransferHandler(
                    new IRecipeTransferInfo<GrillOvenMenu, RecipeHolder<SmokingRecipe>>() {
                        @Override
                        public @NotNull Class<GrillOvenMenu> getContainerClass() {
                            return GrillOvenMenu.class;
                        }

                        @Override
                        public @NotNull Optional<MenuType<GrillOvenMenu>> getMenuType() {
                            return Optional.of(ICContent.MenuTypes.GRILL_OVEN.getType());
                        }

                        @Override
                        public @NotNull RecipeType<RecipeHolder<SmokingRecipe>> getRecipeType() {
                            return RecipeTypes.SMOKING;
                        }

                        @Override
                        public boolean canHandle(GrillOvenMenu container, RecipeHolder<SmokingRecipe> recipe) {
                            return true;
                        }

                        @Override
                        public @NotNull List<Slot> getRecipeSlots(GrillOvenMenu container, RecipeHolder<SmokingRecipe> recipe) {
                            return Collections.singletonList(container.getSlot(slotIndex));
                        }

                        @Override
                        public @NotNull List<Slot> getInventorySlots(GrillOvenMenu container, RecipeHolder<SmokingRecipe> recipe) {
                            List<Slot> slots = new ArrayList<>();
                            for (int i = 4; i < container.slots.size(); i++) {
                                slots.add(container.getSlot(i));
                            }
                            return slots;
                        }
                    });

            IRecipeTransferError error = handler.transferRecipe(container, recipe, recipeSlots, player, maxTransfer, doTransfer);
            if (error == null) {
                success = true;
                if (!maxTransfer) return null;
            } else {
                lasterror = error;
            }
        }

        if (success) return null;
        return lasterror;
    }
}
