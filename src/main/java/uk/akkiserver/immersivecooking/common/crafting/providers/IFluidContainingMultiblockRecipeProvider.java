package uk.akkiserver.immersivecooking.common.crafting.providers;

import dev.architectury.fluid.FluidStack;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IFluidContainingMultiblockRecipeProvider<R extends Recipe<?>> extends IMultiblockRecipeProvider<R> {
    Optional<R> findRecipe(Container container, FluidStack fluid, Level level);

    Optional<R> findRecipe(ItemStack stack, FluidStack fluid, Level level);
}
