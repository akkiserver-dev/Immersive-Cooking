package uk.akkiserver.immersivecooking.common.utils.compat.vinery;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import uk.akkiserver.immersivecooking.common.utils.compat.ICropCompatProvider;

public enum VineryCrops implements ICropCompatProvider {
    RED_GRAPE("red_grape_seeds", "red_grape", "red_grape_bush", 2),
    WHITE_GRAPE("white_grape_seeds", "white_grape", "white_grape_bush", 2),
    SAVANNA_RED_GRAPE("savanna_grape_seeds_red", "savanna_grapes_red", "savanna_grape_bush_red", 2),
    SAVANNA_WHITE_GRAPE("savanna_grape_seeds_white", "savanna_grapes_white", "savanna_grape_bush_white", 2),
    TAIGA_RED_GRAPE("taiga_grape_seeds_red", "taiga_grapes_red", "taiga_grape_bush_red", 2),
    TAIGA_WHITE_GRAPE("taiga_grape_seeds_white", "taiga_grapes_white", "taiga_grape_bush_white", 2),
    JUNGLE_RED_GRAPE("jungle_grape_seeds_red", "jungle_grapes_red", "jungle_grape_bush_red", 2),
    JUNGLE_WHITE_GRAPE("jungle_grape_seeds_white", "jungle_grapes_white", "jungle_grape_bush_white", 2),
    CHERRY("dark_cherry_sapling", "cherry", "dark_cherry_leaves", 3),
    APPLE("apple_tree_sapling", "minecraft:apple", "apple_leaves", 3);

    private final Lazy<ItemStack> seedItem;
    private final Lazy<ItemStack> cropItem;
    private final Lazy<NonNullList<ItemStack>> byProducts;
    private final Lazy<Block> block;
    private final String cropId;
    private final int maxDrop;

    VineryCrops(String seedPath, String cropPath, String blockPath, int maxDrop, String... byProducts) {
        this.cropId = cropPath;
        this.maxDrop = maxDrop;
        this.seedItem = Lazy.of(() -> getItemStack(parseLocation(seedPath)));
        this.cropItem = Lazy.of(() -> getItemStack(parseLocation(cropPath)));
        this.block = Lazy.of(() -> getBlock(parseLocation(blockPath)));
        this.byProducts = Lazy.of(() -> {
            NonNullList<ItemStack> list = NonNullList.create();
            for (String byProduct : byProducts) {
                String[] parts = byProduct.split(":", 2);
                ResourceLocation loc;
                if (parts.length > 1) {
                    loc = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
                } else {
                    loc = ResourceLocation.fromNamespaceAndPath("vinery", parts[0]);
                }
                list.add(getItemStack(loc));
            }
            return list;
        });
    }

    private static ResourceLocation parseLocation(String path) {
        String[] parts = path.split(":", 2);
        if (parts.length > 1) {
            return ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
        } else {
            return ResourceLocation.fromNamespaceAndPath("vinery", parts[0]);
        }
    }

    private static ItemStack getItemStack(ResourceLocation location) {
        Item item = ForgeRegistries.ITEMS.getValue(location);
        return new ItemStack(item != null ? item : Items.AIR);
    }

    private static net.minecraft.world.level.block.Block getBlock(ResourceLocation location) {
        net.minecraft.world.level.block.Block block = ForgeRegistries.BLOCKS.getValue(location);
        return block != null ? block : net.minecraft.world.level.block.Blocks.AIR;
    }

    @Override
    public ItemStack getSeed() {
        return seedItem.get();
    }

    @Override
    public ItemStack getCrop() {
        return cropItem.get();
    }

    @Override
    public net.minecraft.world.level.block.Block getBlock() {
        return block.get();
    }

    @Override
    public NonNullList<ItemStack> getByProducts() {
        return byProducts.get();
    }

    @Override
    public int getMaxDrop() {
        return maxDrop;
    }

    @Override
    public String getCropId() {
        return cropId;
    }

    @Override
    public ResourceLocation getCropLoc() {
        String[] parts = cropId.split(":", 2);
        ResourceLocation loc;
        if (parts.length > 1) {
            loc = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
        } else {
            loc = ResourceLocation.fromNamespaceAndPath("vinery", parts[0]);
        }
        return loc;
    }

    @Override
    public String getModId() {
        return "vinery";
    }
}
