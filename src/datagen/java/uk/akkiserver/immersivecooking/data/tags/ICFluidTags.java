package uk.akkiserver.immersivecooking.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.ICTags;
import uk.akkiserver.immersivecooking.common.fluids.ICFluids;
import uk.akkiserver.immersivecooking.common.utils.compat.vinery.Juices;

import java.util.concurrent.CompletableFuture;

public class ICFluidTags extends FluidTagsProvider {
    public ICFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existing) {
        super(output, provider, ImmersiveCooking.MODID, existing);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (ICFluids.FluidEntry entry : ICFluids.ALL_ENTRIES) {
            tag(TagKey.create(Registries.FLUID, entry.type().getId())).add(entry.getStill(), entry.getFlowing());
        }

        for (Juices juice : Juices.values()) {
            TagKey<Fluid> forgeTag = ICTags.Fluids.create(ResourceLocation.fromNamespaceAndPath("forge", juice.getName()));

            tag(forgeTag).add(
                    juice.getFluidEntry().getStill(),
                    juice.getFluidEntry().getFlowing()
            );
        }
    }
}
