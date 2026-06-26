package uk.akkiserver.immersivecooking.common.compat.farmersdelight;

import uk.akkiserver.immersivecooking.common.compat.ModCompatibility;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public final class FarmersDelightCompat implements ModCompatibility {
    @Override
    public String modId() {
        return "farmersdelight";
    }

    @Override
    public void init() {
        CookpotRecipe.RECIPE_CONVERTERS.add(new FarmersDelightCookpotRecipeConverter());
    }
}
