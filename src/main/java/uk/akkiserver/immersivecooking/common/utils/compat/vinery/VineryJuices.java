package uk.akkiserver.immersivecooking.common.utils.compat.vinery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.ICTags;
import uk.akkiserver.immersivecooking.common.fluids.ICFluids;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import static blusunrize.immersiveengineering.api.utils.TagUtils.createItemWrapper;

public enum VineryJuices {
    RED("red_grapejuice", "red_grape", ICContent.Fluids.RED_GRAPE_JUICE, ICTags.Fluids.RED_GRAPE_JUICE),
    WHITE("white_grapejuice", "white_grape", ICContent.Fluids.WHITE_GRAPE_JUICE, ICTags.Fluids.WHITE_GRAPE_JUICE),
    RED_TAIGA("red_taiga_grapejuice", "taiga_grapes_red", ICContent.Fluids.RED_TAIGA_GRAPE_JUICE, ICTags.Fluids.RED_TAIGA_GRAPE_JUICE),
    WHITE_TAIGA("white_taiga_grapejuice", "taiga_grapes_white", ICContent.Fluids.WHITE_TAIGA_GRAPE_JUICE, ICTags.Fluids.WHITE_TAIGA_GRAPE_JUICE),
    RED_JUNGLE("red_jungle_grapejuice", "jungle_grapes_red", ICContent.Fluids.RED_JUNGLE_GRAPE_JUICE, ICTags.Fluids.RED_JUNGLE_GRAPE_JUICE),
    WHITE_JUNGLE("white_jungle_grapejuice", "jungle_grapes_white", ICContent.Fluids.WHITE_JUNGLE_GRAPE_JUICE, ICTags.Fluids.WHITE_JUNGLE_GRAPE_JUICE),
    RED_SAVANNA("red_savanna_grapejuice", "savanna_grapes_red", ICContent.Fluids.RED_SAVANNA_GRAPE_JUICE, ICTags.Fluids.RED_SAVANNA_GRAPE_JUICE),
    WHITE_SAVANNA("white_savanna_grapejuice", "savanna_grapes_white", ICContent.Fluids.WHITE_SAVANNA_GRAPE_JUICE, ICTags.Fluids.WHITE_SAVANNA_GRAPE_JUICE),
    APPLE("apple_juice", "apple_mash", ICContent.Fluids.APPLE_JUICE, ICTags.Fluids.APPLE_JUICE);

    private final Lazy<ItemStack> juiceItem;
    private final Lazy<ItemStack> juiceIngredient;
    private final ICFluids.FluidEntry juiceFluid;
    private final ResourceLocation juiceId;
    private final ResourceLocation ingredientId;
    private final TagKey<Fluid> fluidTag;

    VineryJuices(String juicePath, String ingredientPath, ICFluids.FluidEntry juiceFluid, TagKey<Fluid> fluidTag) {
        this.juiceId = ResourceLocation.fromNamespaceAndPath("vinery", juicePath);
        this.ingredientId = ResourceLocation.fromNamespaceAndPath("vinery", ingredientPath);
        this.juiceItem = Lazy.of(() -> vineryItem(juicePath));
        this.juiceIngredient = Lazy.of(() -> vineryItem(ingredientPath));
        this.juiceFluid = juiceFluid;
        this.fluidTag = fluidTag;
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

    public ResourceLocation getIngredientId() {
        return ingredientId;
    }

    public Item getItem() {
        return juiceItem.get().getItem();
    }

    public TagKey<Item> getTag() {
        return createItemWrapper(getId());
    }

    public TagKey<Item> getJuiceTag() {
        return createItemWrapper(Resource.mod("juice_ingredients/" + getName()));
    }

    public ItemStack getIngredient() {
        return juiceIngredient.get();
    }

    public ICFluids.FluidEntry getFluidEntry() {
        return juiceFluid;
    }

    public TagKey<Fluid> getFluidTag() {
        return fluidTag;
    }
}