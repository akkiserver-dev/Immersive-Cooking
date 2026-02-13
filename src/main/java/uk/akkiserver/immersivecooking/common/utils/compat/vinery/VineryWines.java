package uk.akkiserver.immersivecooking.common.utils.compat.vinery;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import uk.akkiserver.immersivecooking.common.fluids.ICFluids;

public enum VineryWines {
    APPLE_CIDER("apple_cider", VineryJuices.APPLE, "minecraft:sugar"),
    APPLE_WINE("apple_wine", VineryJuices.APPLE, "vinery:apple_juice"),
    CHERRY_WINE("cherry_wine", VineryJuices.RED, "vinery:cherry"),
    KELP_CIDER("kelp_cider", VineryJuices.WHITE_SAVANNA, "minecraft:kelp"),
    CHORUS_WINE("chorus_wine", VineryJuices.RED_TAIGA, "minecraft:chorus_fruit"),
    MAGNETIC_WINE("magnetic_wine", VineryJuices.RED_JUNGLE, "minecraft:iron_ingot"),
    AEGIS_WINE("aegis_wine", VineryJuices.WHITE_TAIGA, "minecraft:sugar", "minecraft:kelp", "minecraft:iron_ingot"),
    CHENET_WINE("chenet_wine", VineryJuices.RED_JUNGLE, "minecraft:spider_eye", "minecraft:honey_bottle"),
    CLARK_WINE("clark_wine", VineryJuices.WHITE_JUNGLE, "minecraft:sugar"),
    CREEPERS_CRUSH("creepers_crush", VineryJuices.WHITE_SAVANNA, "minecraft:gunpowder"),
    CRISTEL_WINE("cristel_wine", VineryJuices.RED, "minecraft:sugar", "minecraft:feather", "minecraft:blaze_rod"),
    EISWEIN("eiswein", VineryJuices.WHITE_TAIGA, "minecraft:snowball"),
    GLOWING_WINE("glowing_wine", VineryJuices.WHITE, "minecraft:glow_berries"),
    JELLIE_WINE("jellie_wine", VineryJuices.WHITE, "vinery:apple_wine", "vinery:chenet_wine", "vinery:bolvar_wine"),
    JO_SPECIAL_MIXTURE("jo_special_mixture", VineryJuices.RED_SAVANNA, "minecraft:fermented_spider_eye"),
    LILITU_WINE("lilitu_wine", VineryJuices.RED_SAVANNA, "minecraft:honey_bottle", "vinery:cherry"),
    MEAD("mead", VineryJuices.APPLE, "minecraft:honey_bottle", "minecraft:sugar"),
    MELLOHI_WINE("mellohi_wine", VineryJuices.WHITE, "minecraft:sugar", "minecraft:glowstone_dust"),
    NOIR_WINE("noir_wine", VineryJuices.RED, "minecraft:sweet_berries"),
    RED_WINE("red_wine", VineryJuices.RED, "minecraft:sugar"),
    SOLARIS_WINE("solaris_wine", VineryJuices.WHITE, "minecraft:honey_bottle", "minecraft:sweet_berries"),
    STAL_WINE("stal_wine", VineryJuices.RED_JUNGLE, "minecraft:cocoa_beans", "minecraft:sugar"),
    STRAD_WINE("strad_wine", VineryJuices.RED, "minecraft:cocoa_beans", "minecraft:sugar"),
    VILLAGERS_FRIGHT("villagers_fright", VineryJuices.WHITE_JUNGLE, "minecraft:arrow"),
    BOTTLE_MOJANG_NOIR("bottle_mojang_noir", VineryJuices.RED, "minecraft:honey_bottle", "vinery:cherry",
            "vinery:red_wine"),
    BOLVAR_WINE("bolvar_wine", VineryJuices.RED_TAIGA, "minecraft:honey_bottle", "vinery:cherry");

    private final Lazy<ItemStack> wineItem;
    private final ICFluids.FluidEntry wineJuiceIngredient;
    private final ResourceLocation wineId;
    private final Lazy<NonNullList<ItemStack>> wineIngredient;
    private final VineryJuices juice;

    VineryWines(String winePath, VineryJuices juice, String... ingredients) {
        this.wineId = ResourceLocation.fromNamespaceAndPath("vinery", winePath);
        this.wineItem = Lazy.of(() -> getItemStack(this.wineId));
        this.wineJuiceIngredient = juice.getFluidEntry();
        this.wineIngredient = Lazy.of(() -> {
            NonNullList<ItemStack> list = NonNullList.create();
            for (String ingredient : ingredients) {
                String[] parts = ingredient.split(":", 2);
                ResourceLocation loc;
                if (parts.length > 1) {
                    loc = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
                } else {
                    loc = ResourceLocation.fromNamespaceAndPath("minecraft", parts[0]);
                }
                list.add(getItemStack(loc));
            }
            return list;
        });
        this.juice = juice;
    }

    private static ItemStack getItemStack(ResourceLocation location) {
        Item item = ForgeRegistries.ITEMS.getValue(location);
        return new ItemStack(item != null ? item : Items.AIR);
    }

    public ResourceLocation getId() {
        return wineId;
    }

    public ItemStack getItem() {
        return wineItem.get();
    }

    public ICFluids.FluidEntry getJuiceEntry() {
        return wineJuiceIngredient;
    }

    public NonNullList<ItemStack> getIngredients() {
        return wineIngredient.get();
    }

    public VineryJuices getJuice() {
        return juice;
    }
}
