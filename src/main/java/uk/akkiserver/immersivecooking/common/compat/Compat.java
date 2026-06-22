package uk.akkiserver.immersivecooking.common.compat;

import net.neoforged.fml.ModList;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.api.compat.IModCompatibility;
import uk.akkiserver.immersivecooking.common.compat.bnc.BnCCompat;
import uk.akkiserver.immersivecooking.common.compat.farmcharm.FarmCharmCompat;
import uk.akkiserver.immersivecooking.common.compat.farmersdelight.FarmersDelightCompat;

import java.util.ArrayList;
import java.util.List;

public final class Compat {
    public static final List<IModCompatibility> COMPATIBILITIES = new ArrayList<>();

    static {
        COMPATIBILITIES.add(new FarmersDelightCompat());
        COMPATIBILITIES.add(new FarmCharmCompat());
        COMPATIBILITIES.add(new BnCCompat());
    }

    public static void init() {
        for (var compat : COMPATIBILITIES) {
            if (compat.checkAvail()) {
                ImmersiveCooking.LOGGER.info("Registering mod compatibility for '{}'", compat.modId());
                compat.init();
            }
        }
    }
}
