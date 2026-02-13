package uk.akkiserver.immersivecooking.common.utils.compat.farmcharm;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import uk.akkiserver.immersivecooking.common.utils.compat.ICropCompatProvider;

public enum FarmCharmCrops implements ICropCompatProvider {
    TOMATO("tomato_seeds", "tomato", "tomato_crop", 2),
    LETTUCE("lettuce_seeds", "lettuce", "lettuce_crop", 2),
    STRAWBERRY("strawberry_seeds", "strawberry", "strawberry_crop", 2),
    OAT("oat_seeds", "oat", "oat_crop", 2),
    BARLEY("barley_seeds", "barley", "barley_crop", 2),
    CORN("kernels", "corn", "corn_crop", 2),
    ONION("onion", "onion", "onion_crop", 2);

    private final Lazy<ItemStack> seedItem;
    private final Lazy<ItemStack> cropItem;
    private final Lazy<NonNullList<ItemStack>> byProducts;
    private final Lazy<Block> block;
    private final String cropId;
    private final int maxDrop;

    FarmCharmCrops(String seedPath, String cropPath, String blockPath, int maxDrop, String... byProducts) {
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
                    loc = ResourceLocation.fromNamespaceAndPath("farm_and_charm", parts[0]);
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
            return ResourceLocation.fromNamespaceAndPath("farm_and_charm", parts[0]);
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
            loc = ResourceLocation.fromNamespaceAndPath("farm_and_charm", parts[0]);
        }
        return loc;
    }

    @Override
    public String getModId() {
        return "farm_and_charm";
    }
}
