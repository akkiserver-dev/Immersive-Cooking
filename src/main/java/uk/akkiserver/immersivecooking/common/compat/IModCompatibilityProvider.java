package uk.akkiserver.immersivecooking.common.compat;

import net.neoforged.fml.ModList;

public interface IModCompatibilityProvider {
    String modId();
    void init();

    default boolean checkAvail() {
        return ModList.get().isLoaded(modId());
    }
}
