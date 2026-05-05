package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.mixin.IMultiblockRecipeAccessor;

import java.util.List;

public class FoodProcessorRecipe extends MultiblockRecipe {
    public NonNullList<IngredientWithSize> inputs; // 8 slots
    @Nullable
    public final FluidTagInput fluidInput;
    public final ItemStack result;

    public FoodProcessorRecipe(ResourceLocation id, NonNullList<IngredientWithSize> inputs,
                               @Nullable FluidTagInput fluidInput,
                               ItemStack result, int time, int energy) {
        super(Lazy.of(() -> result), ICRecipes.Types.FOOD_PROCESSOR, id);
        this.fluidInput = fluidInput;
        this.result = result;
        this.inputs = inputs;

        ((IMultiblockRecipeAccessor) this).invokeSetTimeAndEnergy(time, energy);
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.FOOD_PROCESSOR.get();
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return NonNullList.of(ItemStack.EMPTY, result);
    }

    @Override
    public List<IngredientWithSize> getItemInputs() {
        return inputs;
    }

    @Override
    public List<FluidTagInput> getFluidInputs() {
        return fluidInput == null ? List.of() : List.of(fluidInput);
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    public boolean shouldCheckItemAvailability() {
        return false;
    }
}
