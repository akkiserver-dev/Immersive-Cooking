package uk.akkiserver.immersivecooking.common.crafting.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultCookpotRecipeProvider extends AbstractMultiblockRecipeProvider<CookpotRecipe> {
    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    protected RecipeType<CookpotRecipe> getRecipeType() {
        return ICRecipes.Types.COOKPOT.get();
    }

    @Override
    protected Class<CookpotRecipe> getRecipeClass() {
        return CookpotRecipe.class;
    }
}
