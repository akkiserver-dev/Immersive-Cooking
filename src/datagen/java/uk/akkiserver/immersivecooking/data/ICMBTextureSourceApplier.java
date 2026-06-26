package uk.akkiserver.immersivecooking.data;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.util.Resource;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ICMBTextureSourceApplier extends SpriteSourceProvider {
	public ICMBTextureSourceApplier(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, ImmersiveCooking.MODID, existingFileHelper);
	}



	@Override
	protected void gather() {
		final SourceList blockAtlas = atlas(SpriteSourceProvider.BLOCKS_ATLAS);

		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/grill_oven"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/cookpot"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/food_fermenter"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/multiblock_base"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/multiblock_components"), Optional.empty()));
	}
}
