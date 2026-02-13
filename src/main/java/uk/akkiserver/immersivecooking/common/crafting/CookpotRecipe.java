package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import java.util.ArrayList;
import uk.akkiserver.immersivecooking.mixin.IMultiblockRecipeAccessor;

public class CookpotRecipe extends MultiblockRecipe {
    public static final CachedRecipeList<CookpotRecipe> RECIPES = new CachedRecipeList<>(ICRecipes.Types.COOKPOT);

    public final NonNullList<IngredientWithSize> inputs;
    public final ItemStack itemOutput;
    public final ItemStack container;

    public CookpotRecipe(ResourceLocation id, NonNullList<IngredientWithSize> inputs, ItemStack itemOutput,
            ItemStack container, int cookTime, int energy) {
        super(Lazy.of(() -> itemOutput), ICRecipes.Types.COOKPOT, id);
        this.inputs = inputs;
        this.itemOutput = itemOutput;
        this.container = container;

        this.setInputListWithSizes(new ArrayList<>(this.inputs));
        ((IMultiblockRecipeAccessor) this).invokeSetTimeAndEnergy(cookTime, energy);

        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.itemOutput));
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.COOKPOT.get();
    }

    @Override
    public boolean matches(net.minecraft.world.Container inv, net.minecraft.world.level.Level level) {
        java.util.List<ItemStack> inventoryCopy = new java.util.ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                inventoryCopy.add(stack.copy());
            }
        }

        for (IngredientWithSize required : this.inputs) {
            int amountNeeded = required.getCount();

            java.util.Iterator<ItemStack> it = inventoryCopy.iterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();

                if (required.test(stack)) {
                    int amountTaken = Math.min(amountNeeded, stack.getCount());

                    amountNeeded -= amountTaken;
                    stack.shrink(amountTaken);

                    if (stack.isEmpty()) {
                        it.remove();
                    }

                    if (amountNeeded <= 0) {
                        break;
                    }
                }
            }

            if (amountNeeded > 0) {
                return false;
            }
        }

        return true;
    }
}