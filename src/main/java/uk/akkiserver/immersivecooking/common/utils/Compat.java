package uk.akkiserver.immersivecooking.common.utils;

import net.minecraftforge.fml.ModList;

public final class Compat {
    public static boolean isFarmCharmInstalled() {
        return ModList.get().isLoaded("farmcharm");
    }

    public static boolean isFarmersDelightInstalled() {
        return ModList.get().isLoaded("farmersdelight");
    }

    public static boolean isVineryInstalled() {
        return ModList.get().isLoaded("vinery");
    }
}
