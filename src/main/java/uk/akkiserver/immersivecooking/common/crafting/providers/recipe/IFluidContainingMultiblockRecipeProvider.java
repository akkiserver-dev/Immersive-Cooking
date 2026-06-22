package uk.akkiserver.immersivecooking.common.crafting.providers.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public interface IFluidContainingMultiblockRecipeProvider<R extends Recipe<?>> extends IMultiblockRecipeProvider<R> {
    Optional<RecipeHolder<R>> findRecipe(RecipeInput stack, FluidStack fluid, Level level);
}
