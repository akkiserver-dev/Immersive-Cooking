package uk.akkiserver.immersivecooking.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;

@Mixin(MultiblockRecipe.class)
public interface IMultiblockRecipeAccessor {
    @Invoker(value = "setTimeAndEnergy", remap = false)
    void invokeSetTimeAndEnergy(int time, int energy);
}
