package uk.akkiserver.immersivecooking.common.crafting.providers.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.utils.Compat;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import umpaz.brewinandchewin.common.crafting.KegFermentingRecipe;
import umpaz.brewinandchewin.common.crafting.KegPouringRecipe;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.brewinandchewin.common.registry.BnCRecipeTypes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BnCFermenterRecipeProvider implements IFluidContainingMultiblockRecipeProvider<FoodFermenterRecipe> {
    private static final int DEFAULT_COOK_TIME = 200;
    private static final int DEFAULT_ENERGY = 800;

    @Override
    public boolean canProvide() {
        return Compat.isBnCInstalled();
    }

    @Override
    public boolean hasMultiInput() {
        return true;
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(Container container, Level level) {
        if (!canProvide() || container.isEmpty()) return Optional.empty();

        return getAllRecipes(level).stream()
                .filter(recipe -> recipe.matches(container, level))
                .findFirst();
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(ItemStack stack, Level level) {
        if (!canProvide() || stack.isEmpty()) return Optional.empty();

        return level.getRecipeManager()
                .getAllRecipesFor(BnCRecipeTypes.FERMENTING.get())
                .stream()
                .filter(r -> ItemStack.isSameItem(r.getResultItem(level.registryAccess()), stack))
                .findFirst()
                .map(bncRecipe -> toFoodFermentRecipe(bncRecipe, level));
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(Container container, FluidStack fluid, Level level) {
        if (!canProvide() || container.isEmpty()) return Optional.empty();

        List<FoodFermenterRecipe> allRecipes = getAllRecipes(level);

        for (FoodFermenterRecipe recipe : allRecipes) {
            boolean itemMatch = recipe.matches(container, level);

            boolean hasFluidReq = recipe.fluidInput != null;

            if (itemMatch) {
                if (hasFluidReq) {
                    boolean typeMatch = recipe.fluidInput.test(fluid);
                    boolean amountMatch = fluid.getAmount() >= recipe.fluidInput.getAmount();
                    if (typeMatch && amountMatch) {
                        return Optional.of(recipe);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<FoodFermenterRecipe> findRecipe(ItemStack stack, FluidStack fluid, Level level) {
        if (!canProvide() || stack.isEmpty()) return Optional.empty();

        for (FoodFermenterRecipe recipe : getAllRecipes(level)) {
            if (recipe.container.getItem() == stack.getItem() && recipe.fluidInput != null) {
                if (recipe.fluidInput.test(fluid) && fluid.getAmount() >= recipe.fluidInput.getAmount()) {
                    return Optional.of(recipe);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public FoodFermenterRecipe byKey(ResourceLocation id, Level level) {
        FoodFermenterRecipe cached = FoodFermenterRecipe.RECIPES.getById(level, id);
        if (cached != null)
            return cached;

        ResourceLocation bncId = id;
        if (id.getNamespace().equals("immersivecooking")) {
            bncId = ResourceLocation.fromNamespaceAndPath("brewinandchewin", id.getPath());
        }

        return level.getRecipeManager().byKey(bncId)
                .filter(r -> r instanceof KegFermentingRecipe)
                .map(r -> toFoodFermentRecipe((KegFermentingRecipe) r, level))
                .orElse(null);
    }

    @Override
    public List<FoodFermenterRecipe> getAllRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(BnCRecipeTypes.FERMENTING.get()).stream()
                .map(r -> toFoodFermentRecipe(r, level))
                .collect(Collectors.toList());
    }

    private FoodFermenterRecipe toFoodFermentRecipe(KegFermentingRecipe bncRecipe, Level level) {
        NonNullList<IngredientWithSize> inputs = bncRecipe.getIngredients().stream()
                .map(IngredientWithSize::new)
                .collect(Collectors.toCollection(NonNullList::create));

        ItemStack output = bncRecipe.getResultItem(level.registryAccess());

        ResourceLocation id = Resource.mod(bncRecipe.getId().getPath());

        FluidTagInput fluidTagInput = null;
        FluidStack bncFluid = bncRecipe.getFluidIngredient();

        if (bncFluid != null && !bncFluid.isEmpty()) {
            ResourceLocation fluidResLoc = ForgeRegistries.FLUIDS.getKey(bncFluid.getFluid());
            if (fluidResLoc != null) {
                fluidTagInput = new FluidTagInput(
                        Either.right(List.of(fluidResLoc)),
                        bncFluid.getAmount(),
                        null
                );
            }
        }

        ItemStack finalOutput = output;
        ItemStack container = level.getRecipeManager()
                .getAllRecipesFor(BnCRecipeTypes.KEG_POURING.get())
                .stream()
                .filter(r -> ItemStack.isSameItem(r.getResultItem(level.registryAccess()), finalOutput))
                .findFirst()
                .map(KegPouringRecipe::getContainer)
                .orElse(ItemStack.EMPTY);

        if (bncRecipe.getResultFluid() != null && container.is(BnCItems.TANKARD.get())) {
            output = output.copyWithCount(bncRecipe.getAmount() / 250);
            container = container.copyWithCount(output.getCount());
        }

        int fermentTime = bncRecipe.getFermentTime();
        if (fermentTime <= 0) {
            fermentTime = DEFAULT_COOK_TIME;
            ImmersiveCooking.LOGGER.warn("[BnC Compat] bncRecipe ferment time is zero, fallback to default time...");
        }

        return new FoodFermenterRecipe(
                id,
                inputs,
                fluidTagInput,
                output,
                container,
                fermentTime,
                DEFAULT_ENERGY);
    }
}
