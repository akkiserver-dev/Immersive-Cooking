package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import uk.akkiserver.immersivecooking.common.ICRecipes;

import java.util.ArrayList;
import uk.akkiserver.immersivecooking.mixin.accessor.IMultiblockRecipeAccessor;

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
}