package uk.akkiserver.immersivecooking.common;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.crafting.serializers.CookpotRecipeSerializer;
import uk.akkiserver.immersivecooking.common.crafting.serializers.FoodFermenterRecipeSerializer;

import static uk.akkiserver.immersivecooking.common.ICRegisters.RECIPE_TYPES;

public final class ICRecipes {
    public static class Types {
        public static final IERecipeTypes.TypeWithClass<CookpotRecipe> COOKPOT = register("cookpot", CookpotRecipe.class);
        public static final IERecipeTypes.TypeWithClass<FoodFermenterRecipe> FOOD_FERMENTER = register("food_fermenter", FoodFermenterRecipe.class);

        private static <T extends Recipe<?>> IERecipeTypes.TypeWithClass<T> register(String name, Class<T> type) {
            RegistryObject<RecipeType<T>> regObj = RECIPE_TYPES.register(name, () -> new RecipeType<>(){});
            return new IERecipeTypes.TypeWithClass<>(regObj, type);
        }

        public static void forceClassLoad() {}
    }

    public static class Serializers {
        public static final RegistryObject<CookpotRecipeSerializer> COOKPOT = ICRegisters.registerSerializer("cookpot", CookpotRecipeSerializer::new);
        public static final RegistryObject<FoodFermenterRecipeSerializer> FOOD_FERMENTER = ICRegisters.registerSerializer("food_fermenter", FoodFermenterRecipeSerializer::new);
        
        public static void forceClassLoad() {}
    }

    public static void init() {
        Types.forceClassLoad();
        Serializers.forceClassLoad();
    }
}
