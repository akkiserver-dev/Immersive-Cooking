package uk.akkiserver.immersivecooking.data.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import uk.akkiserver.immersivecooking.common.crafting.CookpotRecipe;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CookpotRecipeBuilder extends ICMultiblockRecipeBuilder<CookpotRecipeBuilder, CookpotRecipe> {

    public static CookpotRecipeBuilder builder(@Nonnull ItemStack result) {
        return new CookpotRecipeBuilder(result);
    }

    public static CookpotRecipeBuilder builder(@Nonnull ItemLike result) {
        return new CookpotRecipeBuilder(new ItemStack(result));
    }

    private final ItemStack result;
    private ItemStack container = ItemStack.EMPTY;

    private CookpotRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    @Override
    protected CookpotRecipe makeInstance() {
        Objects.requireNonNull(this.result, "CookpotRecipe must have a result item.");
        if (this.inputs.isEmpty())
            throw new IllegalStateException("CookpotRecipe must have at least one input.");

        validateTimeAndEnergy();

        return new CookpotRecipe(getInputs(), this.result, this.container, this.time, this.energy);
    }

    public CookpotRecipeBuilder setContainer(ItemLike container) {
        return setContainer(new ItemStack(container));
    }

    public CookpotRecipeBuilder setContainer(ItemStack container) {
        this.container = container;
        return this;
    }
}