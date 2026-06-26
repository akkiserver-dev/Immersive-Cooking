package uk.akkiserver.immersivecooking.common.compat.farmcharm;

import uk.akkiserver.immersivecooking.common.compat.IModCompatibilityProvider;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public final class FarmCharmCompat implements IModCompatibilityProvider {
    @Override
    public String modId() {
        return "farm_and_charm";
    }

    @Override
    public void init() {
        FarmCharmCookpotRecipeConverter.addAll(CookpotRecipe.RECIPE_CONVERTERS);
    }
}
