package uk.akkiserver.immersivecooking.data.recipes;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class ICGenericBuilder<B extends ICGenericBuilder<B, R>, R extends Recipe<?>> {

    protected final List<ICondition> conditions = new ArrayList<>();

    public B addCondition(ICondition condition) {
        this.conditions.add(condition);
        return (B) this;
    }

    public ICondition[] getConditions() {
        return this.conditions.toArray(ICondition[]::new);
    }

    protected abstract R makeInstance();

    public void build(RecipeOutput out, ResourceLocation name) {
        build(out, null, name);
    }

    public void build(RecipeOutput out, AdvancementHolder adv, ResourceLocation name) {
        out.accept(name, makeInstance(), adv, getConditions());
    }
}