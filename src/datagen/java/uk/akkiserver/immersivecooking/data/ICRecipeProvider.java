package uk.akkiserver.immersivecooking.data;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.client.utils.ClocheRenderFunctions;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.data.recipes.builder.BottlingMachineRecipeBuilder;
import blusunrize.immersiveengineering.data.recipes.builder.ClocheRecipeBuilder;
import blusunrize.immersiveengineering.data.recipes.builder.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.data.recipes.builder.SqueezerRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.FluidType;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.ICTags;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import uk.akkiserver.immersivecooking.api.compat.ICropCompatProvider;
import uk.akkiserver.immersivecooking.common.compat.farmcharm.FarmCharmCrops;
import uk.akkiserver.immersivecooking.common.compat.vinery.VineryCrops;
import uk.akkiserver.immersivecooking.common.compat.vinery.VineryJuices;
import uk.akkiserver.immersivecooking.common.compat.vinery.VineryWines;
import uk.akkiserver.immersivecooking.data.recipes.*;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ICRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ICRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> holderLookupProvider) {
        super(output, holderLookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        buildSqueezerRecipes(recipeOutput);
        buildBottlingRecipes(recipeOutput);
        buildCrusherRecipes(recipeOutput);
        buildFoodFermenterRecipes(recipeOutput);
        buildClocheRecipes(recipeOutput);
    }

    private void buildFoodFermenterRecipes(RecipeOutput recipeOutput) {
        FoodFermenterRecipeBuilder.builder(ModBlocks.RICH_SOIL.get())
                .addCondition(new ModLoadedCondition("vinery"))
                .setFluidInput(FluidTags.WATER, 1000)
                .addInput(IngredientWithSize.of(new ItemStack(ModBlocks.ORGANIC_COMPOST.get())))
                .setTimeAndEnergy(6000, 12800)
                .build(recipeOutput, fd("food_fermenting/rich_soil"));

        for (VineryWines wine : VineryWines.values()) {
            FoodFermenterRecipeBuilder builder = FoodFermenterRecipeBuilder.builder(wine.getItem())
                    .addCondition(new ModLoadedCondition("vinery"))
                    .setFluidInput(wine.getJuice().getFluidTag(), 250)
                    .setTimeAndEnergy(4800, 9600)
                    .setContainer(ObjectRegistry.WINE_BOTTLE.get());

            for (ItemStack ingredient : wine.getIngredients()) {
                builder.addInput(IngredientWithSize.of(ingredient));
            }

            builder.build(recipeOutput, vinery("food_fermenting/" + wine.getId().getPath()));
        }
    }

    private void buildClocheRecipes(RecipeOutput recipeOutput) {
        List<ICropCompatProvider> allCrops = new ArrayList<>();
        allCrops.addAll(List.of(VineryCrops.values()));
        allCrops.addAll(List.of(FarmCharmCrops.values()));

        for (var crop : allCrops) {
            ClocheRecipeBuilder builder = ClocheRecipeBuilder.builder()
                    .output(crop.getCrop().copyWithCount(crop.getMaxDrop()))
                    .addCondition(new ModLoadedCondition(crop.getModId()))
                    .seed(crop.getSeed().getItem())
                    .soil(Blocks.DIRT)
                    .setRender(new ClocheRenderFunctions.RenderFunctionCrop(crop.getBlock()))
                    .setTime(800);

            if (crop.getCrop().is(ICTags.Items.GRAIN) || crop.getSeed().is(ICTags.Items.GRAIN)) {
                builder.output(crop.getSeed());
            }

            if (crop.getCropId().contains("grape")) {
                builder.setTime(1200); // 1 minute for vinery grapes
            }

            builder.build(recipeOutput, Resource.mod("compat/" + crop.getModId() + "/" + crop.getCropLoc().getPath()));
        }
    }

    private void buildCrusherRecipes(RecipeOutput recipeOutput) {
        ItemStack appleMash = ObjectRegistry.APPLE_MASH.get().getDefaultInstance();
        CrusherRecipeBuilder.builder()
                .addCondition(new ModLoadedCondition("vinery"))
                .output(appleMash)
                .input(Items.APPLE)
                .addSecondary(IngredientWithSize.of(appleMash), 0.1f)
                .setEnergy(9600)
                .build(recipeOutput, vinery("crusher/apple_mash"));
    }

    private void buildSqueezerRecipes(RecipeOutput recipeOutput) {
        for (VineryJuices juice : VineryJuices.values()) {
            SqueezerRecipeBuilder.builder()
                    .addCondition(new ModLoadedCondition("vinery"))
                    .output(juice.getFluidEntry().getStill(), 250)
                    .input(juice.getJuiceTag())
                    .setEnergy(9600)
                    .build(recipeOutput, vinery("squeezer/" + juice.getName()));
        }
    }

    private void buildBottlingRecipes(RecipeOutput recipeOutput) {
        int quarter_bucket = FluidType.BUCKET_VOLUME / 4;

        for (VineryJuices juice : VineryJuices.values()) {
            BottlingMachineRecipeBuilder.builder()
                    .addCondition(new ModLoadedCondition("vinery"))
                    .output(juice.getItem())
                    .input(ObjectRegistry.WINE_BOTTLE.get())
                    .fluidInput(juice.getFluidTag(), quarter_bucket)
                    .build(recipeOutput, vinery("bottling/" + juice.getName()));
        }

        BottlingMachineRecipeBuilder.builder()
                .output(Items.HONEY_BOTTLE)
                .fluidInput(ICTags.Fluids.HONEY, quarter_bucket)
                .input(Items.GLASS_BOTTLE)
                .build(recipeOutput, Resource.mod("bottling/honey_bottle"));

        BottlingMachineRecipeBuilder.builder()
                .output(new ItemStack(Items.HONEY_BLOCK, 4))
                .output(IEItems.Molds.MOLD_PACKING_4)
                .input(IEItems.Molds.MOLD_PACKING_4)
                .fluidInput(ICTags.Fluids.HONEY, FluidType.BUCKET_VOLUME * 4)
                .build(recipeOutput, Resource.mod("bottling/honey_block"));
    }

    private ResourceLocation fd(String id) {
        return Resource.mod("compat/farmersdelight/" + id);
    }

    private ResourceLocation fc(String id) {
        return Resource.mod("compat/farm_and_charm/" + id);
    }

    private ResourceLocation vinery(String id) {
        return Resource.mod("compat/vinery/" + id);
    }
}
