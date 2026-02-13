package uk.akkiserver.immersivecooking.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.ArrayList;
import java.util.List;

public final class ICTags {
    private static ResourceLocation forge(String id) {
        return ResourceLocation.fromNamespaceAndPath("forge", id);
    }

    private static ResourceLocation mod(String id) {
        return Resource.mod(id);
    }

    public static class Items {
        public static final List<TagKey<Item>> ALL_ITEM_TAGS = new ArrayList<>();
        public static final TagKey<Item> APPLE_INGREDIENT = createJuiceTag("apple_juice");
        public static final TagKey<Item> RED_GRAPE_INGREDIENT = createJuiceTag("red_grapejuice");
        public static final TagKey<Item> WHITE_GRAPE_INGREDIENT = createJuiceTag("white_grapejuice");
        public static final TagKey<Item> RED_TAIGA_GRAPE_INGREDIENT = createJuiceTag("red_taiga_grapejuice");
        public static final TagKey<Item> WHITE_TAIGA_GRAPE_INGREDIENT = createJuiceTag("white_taiga_grapejuice");
        public static final TagKey<Item> RED_JUNGLE_GRAPE_INGREDIENT = createJuiceTag("red_jungle_grapejuice");
        public static final TagKey<Item> WHITE_JUNGLE_GRAPE_INGREDIENT = createJuiceTag("white_jungle_grapejuice");
        public static final TagKey<Item> RED_SAVANNA_GRAPE_INGREDIENT = createJuiceTag("red_savanna_grapejuice");
        public static final TagKey<Item> WHITE_SAVANNA_GRAPE_INGREDIENT = createJuiceTag("white_savanna_grapejuice");
        public static final TagKey<Item> GRAIN = create(forge("grain"));

        private static TagKey<Item> create(ResourceLocation name) {
            TagKey<Item> tag = ItemTags.create(name);
            ALL_ITEM_TAGS.add(tag);
            return tag;
        }

        private static TagKey<Item> createJuiceTag(String name) {
            return create(Resource.mod("juice_ingredients/" + name));
        }
    }

    public static class Fluids {
        public static final List<TagKey<Fluid>> ALL_FLUID_TAGS = new ArrayList<>();
        public static final TagKey<Fluid> APPLE_JUICE = create(forge("apple_juice"));
        public static final TagKey<Fluid> RED_GRAPE_JUICE = create(forge("red_grapejuice"));
        public static final TagKey<Fluid> RED_TAIGA_GRAPE_JUICE = create(forge("red_taiga_grapejuice"));
        public static final TagKey<Fluid> RED_JUNGLE_GRAPE_JUICE = create(forge("red_jungle_grapejuice"));
        public static final TagKey<Fluid> RED_SAVANNA_GRAPE_JUICE = create(forge("red_savanna_grapejuice"));
        public static final TagKey<Fluid> WHITE_GRAPE_JUICE = create(forge("white_grapejuice"));
        public static final TagKey<Fluid> WHITE_TAIGA_GRAPE_JUICE = create(forge("white_taiga_grapejuice"));
        public static final TagKey<Fluid> WHITE_JUNGLE_GRAPE_JUICE = create(forge("white_jungle_grapejuice"));
        public static final TagKey<Fluid> WHITE_SAVANNA_GRAPE_JUICE = create(forge("white_savanna_grapejuice"));

        public static TagKey<Fluid> create(ResourceLocation name) {
            TagKey<Fluid> tag = FluidTags.create(name);
            ALL_FLUID_TAGS.add(tag);
            return tag;
        }
    }
}
