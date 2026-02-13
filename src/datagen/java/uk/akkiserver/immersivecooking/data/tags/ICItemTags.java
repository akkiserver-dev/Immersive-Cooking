package uk.akkiserver.immersivecooking.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.utils.Resource;
import uk.akkiserver.immersivecooking.common.utils.compat.vinery.VineryJuices;

import java.util.concurrent.CompletableFuture;

public class ICItemTags extends ItemTagsProvider {
    public ICItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blocks, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blocks, ImmersiveCooking.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (var juice : VineryJuices.values()) {
            tag(juice.getJuiceTag()).add(juice.getIngredient().getItem());
        }
    }

    private static ResourceLocation vinery(String id) {
        return ResourceLocation.fromNamespaceAndPath("vinery", id);
    }

    private static ResourceLocation mc(String id) {
        return Resource.mc(id);
    }
}
