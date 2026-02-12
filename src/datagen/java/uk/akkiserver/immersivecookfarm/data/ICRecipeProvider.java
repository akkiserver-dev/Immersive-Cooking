package uk.akkiserver.immersivecookfarm.data;

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
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;
import uk.akkiserver.immersivecookfarm.common.utils.compat.vinery.Juices;

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
                .addCondition(new ModLoadedCondition("vinery"))
                .addSecondary(appleMash, 0.1f)
                .setEnergy(9600)
                .build(writer, Resource.mod("compat/vinery/crusher/apple_mash"));
    }

    private void buildSqueezerRecipes(Consumer<FinishedRecipe> writer) {
        for (Juices juice : Juices.values()) {
            SqueezerRecipeBuilder.builder(juice.getFluidEntry().getStill(), 250)
                    .addCondition(new ModLoadedCondition("vinery"))
                    .addInput(juice.getIngredient())
                    .setEnergy(9600)
                    .build(writer, Resource.mod("compat/vinery/squeezer/" + juice.getName()));
        }
    }

    private void buildBottlingRecipes(Consumer<FinishedRecipe> writer) {
        for (Juices juice : Juices.values()) {
            BottlingMachineRecipeBuilder.builder(juice.getFluidEntry().getBucket())
                    .addCondition(new ModLoadedCondition("vinery"))
                    .addFluid(juice.getFluidEntry().getStill(), 1000)
                    .addInput(Items.BUCKET)
                    .setEnergy(6400)
                    .build(writer, Resource.mod("compat/vinery/bottling/" + juice.getName() + "_bucket"));

            BottlingMachineRecipeBuilder.builder(juice.getItem())
                    .addCondition(new ModLoadedCondition("vinery"))
                    .addFluid(juice.getFluidEntry().getStill(), 250)
                    .addInput(ObjectRegistry.WINE_BOTTLE.get())
                    .setEnergy(6400)
                    .build(writer, Resource.mod("compat/vinery/bottling/" + juice.getName()));
        }
    }
}
