package uk.akkiserver.immersivecookfarm.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import uk.akkiserver.immersivecookfarm.common.ICRecipes;
import uk.akkiserver.immersivecookfarm.mixin.IMultiblockRecipeAccessor;


public class FoodFermenterRecipe extends MultiblockRecipe {
    public static final CachedRecipeList<CookpotRecipe> RECIPES = new CachedRecipeList<>(ICRecipes.Types.COOKPOT);

    public NonNullList<IngredientWithSize> inputs; // 6 slots
    public final FluidStack fluidInput;
    public final ItemStack container;
    public final ItemStack itemOutput;

    public FoodFermenterRecipe(ResourceLocation id, NonNullList<IngredientWithSize> inputs, FluidStack fluidInput, ItemStack itemOutput, ItemStack container, int time, int energy) {
        super(Lazy.of(() -> itemOutput), ICRecipes.Types.FOOD_FERMENTER, id);
        this.fluidInput = fluidInput;
        this.container = container;
        this.itemOutput = itemOutput;
        this.inputs = inputs;

        ((IMultiblockRecipeAccessor) this).invokeSetTimeAndEnergy(time, energy);
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.FOOD_FERMENTER.get();
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }
}
