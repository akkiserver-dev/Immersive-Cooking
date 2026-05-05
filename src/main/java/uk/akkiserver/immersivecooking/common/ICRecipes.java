package uk.akkiserver.immersivecooking.common;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegistryObject;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodProcessorRecipe;
import uk.akkiserver.immersivecooking.common.crafting.serializers.CookpotRecipeSerializer;
import uk.akkiserver.immersivecooking.common.crafting.serializers.FoodFermenterRecipeSerializer;
import uk.akkiserver.immersivecooking.common.crafting.serializers.FoodProcessorRecipeSerializer;

import static uk.akkiserver.immersivecooking.common.ICRegisters.RECIPE_TYPES;

public final class ICRecipes {
    public static class Types {
        public static final IERecipeTypes.TypeWithClass<CookpotRecipe> COOKPOT = register("cookpot", CookpotRecipe.class);
        public static final IERecipeTypes.TypeWithClass<FoodFermenterRecipe> FOOD_FERMENTER = register("food_fermenter", FoodFermenterRecipe.class);
        public static final IERecipeTypes.TypeWithClass<FoodProcessorRecipe> FOOD_PROCESSOR = register("food_processor", FoodProcessorRecipe.class);

        private static <T extends Recipe<?>> IERecipeTypes.TypeWithClass<T> register(String name, Class<T> type) {
            DeferredHolder<RecipeType<T>> regObj = RECIPE_TYPES.register(name, () -> new RecipeType<>(){});
            return new IERecipeTypes.TypeWithClass<>(regObj, type);
        }

        public static void forceClassLoad() {}
    }

    public static class Serializers {
        public static final DeferredHolder<RecipeSerializer<?>, CookpotRecipeSerializer> COOKPOT = ICRegisters.registerSerializer("cookpot", CookpotRecipeSerializer::new);
        public static final DeferredHolder<RecipeSerializer<?>, FoodFermenterRecipeSerializer> FOOD_FERMENTER = ICRegisters.registerSerializer("food_fermenter", FoodFermenterRecipeSerializer::new);
        public static final DeferredHolder<RecipeSerializer<?>, FoodProcessorRecipeSerializer> FOOD_PROCESSOR = ICRegisters.registerSerializer("food_processor", FoodProcessorRecipeSerializer::new);
        
        public static void forceClassLoad() {}
    }

    public static void init() {
        Types.forceClassLoad();
        Serializers.forceClassLoad();
    }
}
