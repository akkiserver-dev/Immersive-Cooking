package uk.akkiserver.immersivecooking.common;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

import static uk.akkiserver.immersivecooking.common.ICRegisters.RECIPE_TYPES;

public final class ICRecipes {
    public static class Types {

        private static <T extends net.minecraft.world.item.crafting.Recipe<?>> IERecipeTypes.TypeWithClass<T> register(String name, Class<T> type) {
            RegistryObject<RecipeType<T>> regObj = RECIPE_TYPES.register(name, () -> new RecipeType<>() {});
            return new IERecipeTypes.TypeWithClass<>(regObj, type);
        }
    }

    public static class Serializers {

    }
}
