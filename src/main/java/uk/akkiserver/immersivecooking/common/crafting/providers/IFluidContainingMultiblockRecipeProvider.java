package uk.akkiserver.immersivecooking.common.crafting.providers;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public interface IFluidContainingMultiblockRecipeProvider<R extends Recipe<?>> extends IMultiblockRecipeProvider<R> {
    Optional<R> findRecipe(Container container, FluidStack fluid, Level level);

    Optional<R> findRecipe(ItemStack stack, FluidStack fluid, Level level);
}
