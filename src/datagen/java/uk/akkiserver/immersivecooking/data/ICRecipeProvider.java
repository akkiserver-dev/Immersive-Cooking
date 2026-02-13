package uk.akkiserver.immersivecooking.data;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.builders.BottlingMachineRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.SqueezerRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.crafting.builders.FoodFermenterRecipeBuilder;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import uk.akkiserver.immersivecooking.common.utils.compat.vinery.Juices;
import uk.akkiserver.immersivecooking.common.utils.compat.vinery.Wines;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

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
        buildFoodFermenterRecipes(writer);
    }

    private void buildFoodFermenterRecipes(Consumer<FinishedRecipe> writer) {
        FoodFermenterRecipeBuilder.builder(ModBlocks.RICH_SOIL.get())
                .addCondition(new ModLoadedCondition("vinery"))
                .setFluidInput(FluidTags.WATER, 1000)
                .addInput(ModBlocks.ORGANIC_COMPOST.get())
                .setEnergy(12800)
                .setTime(6000)
                .build(writer, fd("food_fermenting/rich_soil"));

        for (Wines wine : Wines.values()) {
            FoodFermenterRecipeBuilder builder = FoodFermenterRecipeBuilder.builder(wine.getItem())
                    .addCondition(new ModLoadedCondition("vinery"))
                    .setFluidInput(wine.getJuice().getFluidTag(), 250)
                    .setTime(4800)
                    .setEnergy(9600)
                    .setContainer(ObjectRegistry.WINE_BOTTLE.get());

            for (ItemStack ingredient : wine.getIngredients()) {
                builder.addInput(ingredient);
            }

            builder.build(writer, vinery("food_fermenting/" + wine.getId().getPath()));
        }
    }

    private void buildCrusherRecipes(Consumer<FinishedRecipe> writer) {
        ItemStack appleMash = ObjectRegistry.APPLE_MASH.get().getDefaultInstance();
        CrusherRecipeBuilder.builder(appleMash)
                .addCondition(new ModLoadedCondition("vinery"))
                .addInput(Items.APPLE)
                .addSecondary(appleMash, 0.1f)
                .setEnergy(9600)
                .build(writer, vinery("crusher/apple_mash"));
    }

    private void buildSqueezerRecipes(Consumer<FinishedRecipe> writer) {
        for (Juices juice : Juices.values()) {
            SqueezerRecipeBuilder.builder(juice.getFluidEntry().getStill(), 250)
                    .addCondition(new ModLoadedCondition("vinery"))
                    .addInput(juice.getJuiceTag())
                    .setEnergy(9600)
                    .build(writer, vinery("squeezer/" + juice.getName()));
        }
    }

    private void buildBottlingRecipes(Consumer<FinishedRecipe> writer) {
        for (Juices juice : Juices.values()) {
            BottlingMachineRecipeBuilder.builder(juice.getFluidEntry().getBucket())
                    .addCondition(new ModLoadedCondition("vinery"))
                    .addFluidTag(juice.getFluidTag(), 1000)
                    .addInput(Items.BUCKET)
                    .setEnergy(6400)
                    .build(writer, vinery("bottling/" + juice.getName() + "_bucket"));

            BottlingMachineRecipeBuilder.builder(juice.getItem())
                    .addCondition(new ModLoadedCondition("vinery"))
                    .addFluidTag(juice.getFluidTag(), 250)
                    .addInput(ObjectRegistry.WINE_BOTTLE.get())
                    .setEnergy(6400)
                    .build(writer, vinery("bottling/" + juice.getName()));
        }
    }

    private ResourceLocation fd(String id) {
        return Resource.mod("compat/farmersdelight/" + id);
    }

    private ResourceLocation vinery(String id) {
        return Resource.mod("compat/vinery/" + id);
    }
}
