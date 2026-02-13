package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.mixin.IMultiblockRecipeAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FoodFermenterRecipe extends MultiblockRecipe {
    public static final CachedRecipeList<FoodFermenterRecipe> RECIPES = new CachedRecipeList<>(
            ICRecipes.Types.FOOD_FERMENTER);

    public NonNullList<IngredientWithSize> inputs; // 6 slots
    @Nullable
    public final FluidTagInput fluidInput;
    public final ItemStack container;
    public final ItemStack result;

    public FoodFermenterRecipe(ResourceLocation id, NonNullList<IngredientWithSize> inputs,
                               @Nullable FluidTagInput fluidInput,
                               ItemStack result, ItemStack container, int time, int energy) {
        super(Lazy.of(() -> result), ICRecipes.Types.FOOD_FERMENTER, id);
        this.fluidInput = fluidInput;
        this.container = container;
        this.result = result;
        this.inputs = inputs;

        ((IMultiblockRecipeAccessor) this).invokeSetTimeAndEnergy(time, energy);
    }

    @Override
    public boolean matches(Container inv, Level level) {
        List<ItemStack> inventoryCopy = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                inventoryCopy.add(stack.copy());
            }
        }

        for (IngredientWithSize required : this.inputs) {
            int amountNeeded = required.getCount();

            Iterator<ItemStack> it = inventoryCopy.iterator();
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

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return ICRecipes.Serializers.FOOD_FERMENTER.get();
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
