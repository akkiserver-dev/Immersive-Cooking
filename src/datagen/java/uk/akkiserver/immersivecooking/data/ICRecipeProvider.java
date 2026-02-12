package uk.akkiserver.immersivecooking.data;

import blusunrize.immersiveengineering.api.crafting.builders.BottlingMachineRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.SqueezerRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.function.Consumer;

public class ICRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ICRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> writer) {
        buildSqueezerRecipes(writer);
        buildBottlingRecipes(writer);
        buildCrusherRecipes(writer);
    }

    private void buildCrusherRecipes(Consumer<FinishedRecipe> writer) {
        ItemStack appleMash = ObjectRegistry.APPLE_MASH.get().getDefaultInstance();
        CrusherRecipeBuilder.builder(appleMash)
                .addSecondary(appleMash, 0.1f)
                .build(writer, Resource.mod("crusher/compat/vinery/apple_mash"));
    }

    private void buildSqueezerRecipes(Consumer<FinishedRecipe> writer) {
        Fluid appleJuiceFluid = ICContent.Fluids.APPLE_JUICE.getStill();
        ItemStack appleMash = ObjectRegistry.APPLE_MASH.get().getDefaultInstance();
        SqueezerRecipeBuilder.builder(appleJuiceFluid, 250)
                .addInput(appleMash)
                .setEnergy(12000)
                .build(writer, Resource.mod("squeezer/compat/vinery/apple_juice_fluid"));
    }

    private void buildBottlingRecipes(Consumer<FinishedRecipe> writer) {
        Fluid appleJuiceFluid = ICContent.Fluids.APPLE_JUICE.getStill();
        ItemStack appleJuiceBucket = ICContent.Fluids.APPLE_JUICE.getBucket().getDefaultInstance();
        ItemStack appleJuice = ObjectRegistry.APPLE_JUICE.get().getDefaultInstance();
        BottlingMachineRecipeBuilder.builder(appleJuiceBucket)
                .addFluid(appleJuiceFluid, 1000)
                .addInput(Items.BUCKET)
                .build(writer, Resource.mod("bottling/compat/vinery/apple_juicebucket"));

        BottlingMachineRecipeBuilder.builder(appleJuice)
                .addFluid(appleJuiceFluid, 250)
                .addInput(ObjectRegistry.WINE_BOTTLE.get())
                .build(writer, Resource.mod("bottling/compat/vinery/apple_juice"));
    }
}
