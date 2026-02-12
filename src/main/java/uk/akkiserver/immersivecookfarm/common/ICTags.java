package uk.akkiserver.immersivecookfarm.common;

import net.minecraft.resources.ResourceLocation;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

public final class ICTags {
    private static ResourceLocation forge(String id) {
        return ResourceLocation.fromNamespaceAndPath("forge", id);
    }

    private static ResourceLocation mod(String id) {
        return Resource.mod(id);
    }
}
