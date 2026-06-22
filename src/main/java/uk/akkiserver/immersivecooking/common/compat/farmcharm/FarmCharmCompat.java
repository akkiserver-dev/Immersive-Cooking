package uk.akkiserver.immersivecooking.common.compat.farmcharm;

import net.neoforged.fml.ModList;
import uk.akkiserver.immersivecooking.api.compat.IModCompatibility;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

public final class FarmCharmCompat implements IModCompatibility {
    @Override
    public String modId() {
        return "farm_and_charm";
    }

    @Override
    public boolean checkAvail() {
        return ModList.get().isLoaded(modId());
    }

    @Override
    public void init() {
        FarmCharmCookpotRecipeConverter.addAll(CookpotRecipe.RECIPE_CONVERTERS);
    }
}
