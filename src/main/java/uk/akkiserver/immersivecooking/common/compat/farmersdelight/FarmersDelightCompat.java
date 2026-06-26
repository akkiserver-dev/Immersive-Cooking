package uk.akkiserver.immersivecooking.common.compat.farmersdelight;

import uk.akkiserver.immersivecooking.common.compat.IModCompatibilityProvider;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public final class FarmersDelightCompat implements IModCompatibilityProvider {
    @Override
    public String modId() {
        return "farmersdelight";
    }

    @Override
    public void init() {
        CookpotRecipe.RECIPE_CONVERTERS.add(new FarmersDelightCookpotRecipeConverter());
    }
}
