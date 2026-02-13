package uk.akkiserver.immersivecooking.common.crafting.providers;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;
import uk.akkiserver.immersivecooking.common.utils.Compat;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FDCookpotRecipeProvider implements IMultiblockRecipeProvider<CookpotRecipe> {
    private static final Lazy<RecipeManager.CachedCheck<RecipeWrapper, CookingPotRecipe>> cookingPotCheckerLazySupplier = Lazy
            .of(() -> RecipeManager.createCheck(ModRecipeTypes.COOKING.get()));

    @Override
    public boolean canProvide() {
        return Compat.isFarmersDelightInstalled();
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(Container container, Level level) {
        if (!canProvide() || container.isEmpty())
            return Optional.empty();

        return getAllRecipes(level).stream()
                .filter(recipe -> recipe.matches(container, level))
                .findFirst();
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(ItemStack stack, Level level) {
        if (!canProvide() || stack.isEmpty())
            return Optional.empty();

        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
                .filter(fdRecipe -> ItemStack.isSameItem(fdRecipe.getResultItem(level.registryAccess()), stack))
                .findFirst()
                .map(fdRecipe -> toCookpotRecipe(fdRecipe, level, fdRecipe.getCookTime() * 8));
    }

    @Override
    public CookpotRecipe byKey(ResourceLocation id, Level level) {
        CookpotRecipe cached = CookpotRecipe.RECIPES.getById(level, id);
        if (cached != null)
            return cached;

        ResourceLocation fdId = id;
        if (id.getNamespace().equals("immersivecooking")) {
            fdId = ResourceLocation.fromNamespaceAndPath("farmersdelight", id.getPath());
        }

        return level.getRecipeManager().byKey(fdId)
                .filter(r -> r instanceof CookingPotRecipe)
                .map(r -> toCookpotRecipe((CookingPotRecipe) r, level))
                .orElse(null);
    }

    @Override
    public List<CookpotRecipe> getAllRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
                .map(fdRecipe -> toCookpotRecipe(fdRecipe, level))
                .collect(Collectors.toList());
    }

    private CookpotRecipe toCookpotRecipe(CookingPotRecipe fdRecipe, Level level) {
        return toCookpotRecipe(fdRecipe, level, fdRecipe.getCookTime() * 4);
    }

    private CookpotRecipe toCookpotRecipe(CookingPotRecipe fdRecipe, Level level, int energy) {
        NonNullList<IngredientWithSize> inputs = fdRecipe.getIngredients().stream()
                .map(IngredientWithSize::new)
                .collect(Collectors.toCollection(NonNullList::create));

        ResourceLocation id = Resource.mod(fdRecipe.getId().getPath());

        return new CookpotRecipe(
                id,
                inputs,
                fdRecipe.getResultItem(level.registryAccess()),
                fdRecipe.getOutputContainer(),
                (int) (fdRecipe.getCookTime() * 0.75),
                energy);
    }
}