package uk.akkiserver.immersivecookfarm.common.utils.compat.vinery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.fluids.ICFluids;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

import java.util.function.Supplier;

import static blusunrize.immersiveengineering.api.utils.TagUtils.createItemWrapper;

public enum Juices {
    RED("red_grapejuice", "red_grape", ICContent.Fluids.RED_GRAPE_JUICE),
    WHITE("white_grapejuice", "white_grape", ICContent.Fluids.WHITE_GRAPE_JUICE),
    RED_TAIGA("red_taiga_grapejuice", "taiga_grapes_red", ICContent.Fluids.RED_TAIGA_GRAPE_JUICE),
    WHITE_TAIGA("white_taiga_grapejuice", "taiga_grapes_white", ICContent.Fluids.WHITE_TAIGA_GRAPE_JUICE),
    RED_JUNGLE("red_jungle_grapejuice", "jungle_grapes_red", ICContent.Fluids.RED_JUNGLE_GRAPE_JUICE),
    WHITE_JUNGLE("white_jungle_grapejuice", "jungle_grapes_white", ICContent.Fluids.WHITE_JUNGLE_GRAPE_JUICE),
    RED_SAVANNA("red_savanna_grapejuice", "savanna_grapes_red", ICContent.Fluids.RED_SAVANNA_GRAPE_JUICE),
    WHITE_SAVANNA("white_savanna_grapejuice", "savanna_grapes_white", ICContent.Fluids.WHITE_SAVANNA_GRAPE_JUICE),
    APPLE("apple_juice", "apple_mash", ICContent.Fluids.APPLE_JUICE);

    private final Lazy<ItemStack> juiceItem;
    private final Lazy<ItemStack> juiceIngredient;
    private final ICFluids.FluidEntry juiceFluid;
    private final ResourceLocation juiceId;

    Juices(String juicePath, String ingredientPath, ICFluids.FluidEntry juiceFluid) {
        this.juiceId = ResourceLocation.fromNamespaceAndPath("vinery", juicePath);
        this.juiceItem = Lazy.of(() -> vineryItem(juicePath));
        this.juiceIngredient = Lazy.of(() -> vineryItem(ingredientPath));
        this.juiceFluid = juiceFluid;
    }

    private static ItemStack vineryItem(String path) {
        var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("vinery", path));
        return new ItemStack(item != null ? item : Items.AIR);
    }

    public String getName() {
        return juiceId.getPath();
    }

    public @Nullable ResourceLocation getId() {
        return juiceId;
    }

    public Item getItem() {
        return juiceItem.get().getItem();
    }

    public TagKey<Item> getTag() {
        return createItemWrapper(getId());
    }

    public TagKey<Item> getJuiceTag() {
        String tagName;
        if (getName().contains("grapejuice")) {
            tagName = getName().contains("red") ? "red_grapejuice" : "white_grapejuice";
        } else {
            tagName = "apple_juice";
        }
        return createItemWrapper(Resource.mod("juice_ingredients/" + tagName));
    }

    public ItemStack getIngredient() {
        return juiceIngredient.get();
    }

    public ICFluids.FluidEntry getFluidEntry() {
        return juiceFluid;
    }
}