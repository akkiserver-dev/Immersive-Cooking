package uk.akkiserver.immersivecookfarm.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;
import uk.akkiserver.immersivecookfarm.common.utils.compat.vinery.Juices;

import java.util.concurrent.CompletableFuture;

public class ICItemTags extends ItemTagsProvider {
    public ICItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blocks, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blocks, ImmersiveCookFarm.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (var juice : Juices.values()) {
            tag(juice.getJuiceTag()).add(ResourceKey.create(Registries.ITEM, juice.getId()));
        }
    }

    private static ResourceLocation vinery(String id) {
        return ResourceLocation.fromNamespaceAndPath("vinery", id);
    }

    private static ResourceLocation mc(String id) {
        return Resource.mc(id);
    }
}
