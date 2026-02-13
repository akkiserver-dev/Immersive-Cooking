package uk.akkiserver.immersivecooking.common.crafting.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import uk.akkiserver.immersivecooking.common.ICRecipes;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultCookpotRecipeProvider implements IMultiblockRecipeProvider<CookpotRecipe> {
    private static final Lazy<RecipeManager.CachedCheck<Container, CookpotRecipe>> cookpotRecipeLazySupplier = Lazy.of(() -> RecipeManager.createCheck(ICRecipes.Types.COOKPOT.get()));

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(Container container, Level level) {
        return cookpotRecipeLazySupplier.get().getRecipeFor(container, level);
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(ItemStack stack, Level level) {
        return cookpotRecipeLazySupplier.get().getRecipeFor(new SimpleContainer(stack), level);
    }

    @Override
    public CookpotRecipe byKey(ResourceLocation id, Level level) {
        return (CookpotRecipe) level.getRecipeManager()
                .byKey(id)
                .filter(r -> r instanceof CookpotRecipe)
                .orElse(null);
    }

    @Override
    public List<CookpotRecipe> getAllRecipes(Level level) {
        return new ArrayList<>(level.getRecipeManager().getAllRecipesFor(ICRecipes.Types.COOKPOT.get()));
    }
}
