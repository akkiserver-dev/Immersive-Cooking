package uk.akkiserver.immersivecooking.utils;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.resources.ResourceLocation;
import uk.akkiserver.immersivecooking.ImmersiveCooking;

public final class Resource {
    public static ResourceLocation mod(String id) {
        return ResourceLocation.fromNamespaceAndPath(ImmersiveCooking.MODID, id);
    }

    public static ResourceLocation texture(String id) {
        return mod("textures/" + id);
    }

    public static ResourceLocation ie(String id) {
        return ResourceLocation.fromNamespaceAndPath(Lib.MODID, id);
    }

    public static ResourceLocation mc(String id) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", id);
    };
}
