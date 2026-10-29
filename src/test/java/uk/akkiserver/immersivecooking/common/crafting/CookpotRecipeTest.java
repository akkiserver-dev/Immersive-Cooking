package uk.akkiserver.immersivecooking.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.akkiserver.immersivecooking.common.compat.farmersdelight.FarmersDelightCookpotRecipeConverter;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.HashSet;
import java.util.Set;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CookpotRecipeTest {
    @TempDir
    static Path directory;
    private static MinecraftServer server;
    private static LevelStorageSource.LevelStorageAccess storage;

    @BeforeAll
    static void loadRecipesAndTags() throws Exception {
        storage = LevelStorageSource.createDefault(directory.resolve("world")).validateAndCreateAccess("test");
        var packs = ServerPacksSource.createPackRepository(storage);
        // Load the real mod datapacks and registries without starting a worldless
        // server: IE's ServerStartedEvent handler requires an actual overworld.
        server = EphemeralTestServerProvider.JUnitServer.create(Thread.currentThread(), directory, storage, packs);
    }

    @AfterAll
    static void closeResources() throws Exception {
        if (server != null) {
            ((net.minecraft.server.packs.resources.CloseableResourceManager) server.getResourceManager()).close();
        }
        if (storage != null) storage.close();
    }

    @Test
    void chickenSoupDoesNotMatchCabbageRolls() {
        CookpotRecipe soup = converted(server, "chicken_soup");
        CookpotRecipe rolls = converted(server, "cabbage_rolls");
        for (int count : new int[]{1, 64}) {
            RecipeWrapper inventory = screenshotInputs(count);
            assertTrue(soup.matches(inventory, server.overworld()));
            assertFalse(rolls.matches(inventory, server.overworld()));
            assertConsumptionPlan(soup, inventory);
            // Matching must never consume the inventory.
            for (int i = 0; i < 4; i++)
                assertEquals(count, inventory.getItem(i).getCount());
        }
    }

    @Test
    void loadedRecipeRegistrySelectsChickenSoup() {
        var chosen = CookpotRecipe.findRecipe(screenshotInputs(64), server.overworld()).orElseThrow().value();
        assertEquals(stack("farmersdelight:chicken_soup", 1).getItem(), chosen.result.getItem());
    }

    @Test
    void recipeSelectionChoosesSoupWithBothRecipesIndexed() {
        var previous = new java.util.HashMap<>(CookpotRecipe.RECIPES);
        try {
            CookpotRecipe soup = converted(server, "chicken_soup");
            CookpotRecipe rolls = converted(server, "cabbage_rolls");
            CookpotRecipe.RECIPES.clear();
            var soupId = ResourceLocation.fromNamespaceAndPath("immersivecooking", "test/soup");
            var rollsId = ResourceLocation.fromNamespaceAndPath("immersivecooking", "test/rolls");
            CookpotRecipe.RECIPES.put(rollsId, new RecipeHolder<>(rollsId, rolls));
            CookpotRecipe.RECIPES.put(soupId, new RecipeHolder<>(soupId, soup));
            assertSame(soup, CookpotRecipe.findRecipe(screenshotInputs(64), server.overworld()).orElseThrow().value());
        } finally {
            CookpotRecipe.RECIPES.clear();
            CookpotRecipe.RECIPES.putAll(previous);
        }
    }

    @Test
    void genuineCabbageRollsStillMatch() {
        CookpotRecipe rolls = converted(server, "cabbage_rolls");
        RecipeWrapper inventory = inventory(stack("farmersdelight:cabbage", 64), new ItemStack(Items.CHICKEN, 64));
        assertTrue(rolls.matches(inventory, server.overworld()));
        assertConsumptionPlan(rolls, inventory);
    }

    @Test
    void overlappingIngredientsUseDistinctSlots() {
        // The broad ingredient appears first: greedy allocation would take carrot
        // and leave potato unable to satisfy the carrot-only ingredient.
        CookpotRecipe recipe = recipe(
                new IngredientWithSize(Ingredient.of(Items.CARROT, Items.POTATO)),
                new IngredientWithSize(Ingredient.of(Items.CARROT)));
        RecipeWrapper inventory = inventory(new ItemStack(Items.CARROT, 64), new ItemStack(Items.POTATO, 64));
        assertArrayEquals(new int[]{1, 0}, recipe.findInputSlots(inventory));
        assertConsumptionPlan(recipe, inventory);
    }

    @Test
    void repeatedIngredientsNeedSeparateSlots() {
        CookpotRecipe recipe = recipe(
                new IngredientWithSize(Ingredient.of(Items.CARROT)),
                new IngredientWithSize(Ingredient.of(Items.CARROT)));
        assertNull(recipe.findInputSlots(inventory(new ItemStack(Items.CARROT, 64))));
        RecipeWrapper split = inventory(new ItemStack(Items.CARROT, 64), new ItemStack(Items.CARROT, 64));
        assertConsumptionPlan(recipe, split);
    }

    @Test
    void sizedIngredientsRequireEnoughItemsInTheirSlot() {
        CookpotRecipe recipe = recipe(new IngredientWithSize(Ingredient.of(Items.CARROT), 3));
        assertNull(recipe.findInputSlots(inventory(new ItemStack(Items.CARROT, 2))));
        assertConsumptionPlan(recipe, inventory(new ItemStack(Items.CARROT, 64)));
    }

    @Test
    void emptySlotsAndContainerSlotsDoNotAffectMatching() {
        CookpotRecipe recipe = recipe(new IngredientWithSize(Ingredient.of(Items.CARROT)));
        ItemStackHandler handler = new ItemStackHandler(9);
        handler.setStackInSlot(5, new ItemStack(Items.CARROT, 64));
        handler.setStackInSlot(6, new ItemStack(Items.BOWL, 64));
        handler.setStackInSlot(7, new ItemStack(Items.MUSHROOM_STEW));
        assertArrayEquals(new int[]{5}, recipe.findInputSlots(new RecipeWrapper(handler)));
    }

    @Test
    void extraOrMissingIngredientsAreRejected() {
        CookpotRecipe recipe = recipe(new IngredientWithSize(Ingredient.of(Items.CARROT)));
        assertNull(recipe.findInputSlots(inventory()));
        assertNull(recipe.findInputSlots(inventory(new ItemStack(Items.POTATO))));
        assertNull(recipe.findInputSlots(inventory(new ItemStack(Items.CARROT), new ItemStack(Items.POTATO))));
        assertNull(recipe().findInputSlots(inventory()));
    }

    private static void assertConsumptionPlan(CookpotRecipe recipe, RecipeWrapper inventory) {
        int[] slots = recipe.findInputSlots(inventory);
        assertNotNull(slots);
        assertEquals(recipe.inputs.size(), slots.length);
        Set<Integer> used = new HashSet<>();
        for (int i = 0; i < slots.length; i++) {
            assertTrue(used.add(slots[i]), "Each ingredient must consume a distinct slot");
            assertTrue(recipe.inputs.get(i).test(inventory.getItem(slots[i])));
        }
        int occupied = 0;
        for (int i = 0; i < Math.min(6, inventory.size()); i++)
            if (!inventory.getItem(i).isEmpty()) occupied++;
        assertEquals(occupied, slots.length, "Every occupied input slot must be accounted for");
    }

    @SuppressWarnings("unchecked")
    private static CookpotRecipe converted(MinecraftServer server, String name) {
        var id = ResourceLocation.fromNamespaceAndPath("farmersdelight", "cooking/" + name);
        RecipeHolder<CookingPotRecipe> source = (RecipeHolder<CookingPotRecipe>) server.getRecipeManager().byKey(id).orElseThrow();
        return new FarmersDelightCookpotRecipeConverter()
                .convert(source, server.getRecipeManager(), server.registryAccess()).orElseThrow().value();
    }

    private static CookpotRecipe recipe(IngredientWithSize... ingredients) {
        NonNullList<IngredientWithSize> inputs = NonNullList.create();
        inputs.addAll(java.util.Arrays.asList(ingredients));
        return new CookpotRecipe(inputs, new ItemStack(Items.MUSHROOM_STEW),
                new ItemStack(Items.BOWL), 100, 800);
    }

    private static RecipeWrapper screenshotInputs(int count) {
        return inventory(new ItemStack(Items.CHICKEN, count), new ItemStack(Items.CARROT, count),
                stack("farmersdelight:cabbage", count), stack("farmersdelight:tomato", count));
    }

    private static ItemStack stack(String id, int count) {
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        assertNotEquals(Items.AIR, item, "Missing test ingredient " + id);
        return new ItemStack(item, count);
    }

    private static RecipeWrapper inventory(ItemStack... stacks) {
        ItemStackHandler handler = new ItemStackHandler(6);
        for (int i = 0; i < stacks.length; i++) handler.setStackInSlot(i, stacks[i]);
        return new RecipeWrapper(handler);
    }
}
