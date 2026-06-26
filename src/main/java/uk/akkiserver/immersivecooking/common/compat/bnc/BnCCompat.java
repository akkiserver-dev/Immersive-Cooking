package uk.akkiserver.immersivecooking.common.compat.bnc;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.compat.ModCompatibility;
import uk.akkiserver.immersivecooking.common.crafting.FoodFermenterRecipe;
import uk.akkiserver.immersivecooking.common.util.FluidUtils;
import umpaz.brewinandchewin.common.crafting.FluidIngredientWithAmount;
import umpaz.brewinandchewin.common.utility.AbstractedFluidIngredient;
import umpaz.brewinandchewin.common.utility.AbstractedFluidStack;
import umpaz.brewinandchewin.neoforge.utility.KegCompatibleFluidIngredients;

public final class BnCCompat implements ModCompatibility {
    static SizedFluidIngredient toSizedFluidIngredient(FluidIngredientWithAmount fluidWithAmount) {
        FluidIngredient fluidIngredient = toFluidIngredient(fluidWithAmount.ingredient());
        int amount = (int) fluidWithAmount.loaderAmount(); // mB
        return new SizedFluidIngredient(fluidIngredient, amount);
    }

    static FluidIngredient toFluidIngredient(AbstractedFluidIngredient ingredient) {
        if (ingredient instanceof KegCompatibleFluidIngredients.NeoForgeIngredient) {
            try {
                var field = KegCompatibleFluidIngredients.NeoForgeIngredient.class.getDeclaredField("ingredient");
                field.setAccessible(true);
                return (FluidIngredient) field.get(ingredient);
            } catch (Exception e) {
                ImmersiveCooking.LOGGER.error("[BnC Compat] Failed to extract FluidIngredient via reflection", e);
            }
        }

        if (ingredient instanceof KegCompatibleFluidIngredients.Tag tag) {
            var tagKey = tag.getTagKey();
            if (tagKey != null) {
                return FluidIngredient.tag(tagKey);
            }
        }

        return ingredient.displayStacks().stream()
                .map(AbstractedFluidStack::loaderSpecific)
                .filter(o -> o instanceof FluidStack)
                .map(o -> (FluidStack) o)
                .map(fs -> FluidIngredient.of(fs.getFluid()))
                .findFirst()
                .orElseGet(FluidIngredient::of);
    }

    @Override
    public String modId() {
        return "brewinandchewin";
    }

    @Override
    public void init() {
        FoodFermenterRecipe.RECIPE_CONVERTERS.add(new BnCFermentingRecipeConverter());
        FluidUtils.registerFluidRelationProvider(new BnCFluidRelationProvider());
    }
}
