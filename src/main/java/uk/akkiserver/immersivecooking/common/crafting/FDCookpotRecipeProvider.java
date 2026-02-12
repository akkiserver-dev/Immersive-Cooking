package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.Optional;
import java.util.stream.Collectors;

public class FDCookpotRecipeProvider implements IMultiblockRecipeProvider<CookpotRecipe> {
    private static final Lazy<RecipeManager.CachedCheck<RecipeWrapper, CookingPotRecipe>> cookingPotCheckerLazySupplier = Lazy
            .of(() -> RecipeManager.createCheck(ModRecipeTypes.COOKING.get()));

    @Override
    public boolean canProvide() {
        return ModList.get().isLoaded("farmersdelight");
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(Container container, Level level) {
        if (!canProvide() || container.isEmpty())
            return Optional.empty();

        RecipeWrapper wrapper;
        if (container instanceof RecipeWrapper) {
            wrapper = (RecipeWrapper) container;
        } else {
            wrapper = new RecipeWrapper(new InvWrapper(container));
        }

        RecipeManager.CachedCheck<RecipeWrapper, CookingPotRecipe> cookingPotChecker = cookingPotCheckerLazySupplier
                .get();

        return cookingPotChecker.getRecipeFor(wrapper, level).map(fdRecipe -> {
            NonNullList<IngredientWithSize> inputs = fdRecipe.getIngredients().stream()
                    .map(IngredientWithSize::new)
                    .collect(Collectors.toCollection(NonNullList::create));

            ItemStack output = fdRecipe.getResultItem(level.registryAccess());

            return new CookpotRecipe(
                    fdRecipe.getId(),
                    inputs,
                    output,
                    fdRecipe.getOutputContainer(),
                    (int) (fdRecipe.getCookTime() * 0.75),
                    fdRecipe.getCookTime() * 4);
        });
    }

    @Override
    public Optional<CookpotRecipe> findRecipe(ItemStack stack, Level level) {
        if (!canProvide() || stack.isEmpty())
            return Optional.empty();

        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
                .filter(fdRecipe -> ItemStack.isSameItem(fdRecipe.getResultItem(level.registryAccess()), stack))
                .findFirst()
                .map(fdRecipe -> {
                    NonNullList<IngredientWithSize> inputs = fdRecipe.getIngredients().stream()
                            .map(IngredientWithSize::new)
                            .collect(Collectors.toCollection(NonNullList::create));
                    return new CookpotRecipe(
                            fdRecipe.getId(),
                            inputs,
                            fdRecipe.getResultItem(level.registryAccess()),
                            fdRecipe.getOutputContainer(),
                            (int) (fdRecipe.getCookTime() * 0.75),
                            fdRecipe.getCookTime() * 8);
                });
    }

    @Override
    public CookpotRecipe byKey(ResourceLocation id, Level level) {
        CookpotRecipe cached = CookpotRecipe.RECIPES.getById(level, id);
        if (cached != null)
            return cached;

        return level.getRecipeManager().byKey(id)
                .filter(r -> r instanceof CookingPotRecipe)
                .map(r -> {
                    var fdRecipe = (CookingPotRecipe) r;
                    NonNullList<IngredientWithSize> inputs = fdRecipe
                            .getIngredients().stream()
                            .map(IngredientWithSize::new)
                            .collect(Collectors.toCollection(NonNullList::create));
                    return new CookpotRecipe(fdRecipe.getId(), inputs,
                            fdRecipe.getResultItem(level.registryAccess()), fdRecipe.getOutputContainer(),
                            (int) (fdRecipe.getCookTime() * 0.75),
                            fdRecipe.getCookTime() * 4);
                }).orElse(null);
    }
}
