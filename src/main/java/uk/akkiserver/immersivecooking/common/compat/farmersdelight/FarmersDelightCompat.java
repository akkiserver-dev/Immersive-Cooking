package uk.akkiserver.immersivecooking.common.compat.farmersdelight;

import net.neoforged.fml.ModList;
import uk.akkiserver.immersivecooking.api.compat.IModCompatibility;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public final class FarmersDelightCompat implements IModCompatibility {
    @Override
    public String modId() {
        return "farmersdelight";
    }

    @Override
    public boolean checkAvail() {
        return ModList.get().isLoaded(modId());
    }

    @Override
    public void init() {
        CookpotRecipe.RECIPE_CONVERTERS.add(new FarmersDelightCookpotRecipeConverter());
    }
}
