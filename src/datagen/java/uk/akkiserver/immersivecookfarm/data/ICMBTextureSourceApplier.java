package uk.akkiserver.immersivecookfarm.data;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import uk.akkiserver.immersivecookfarm.ImmersiveCookFarm;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

import java.util.Optional;

public class ICMBTextureSourceApplier extends SpriteSourceProvider{
	public ICMBTextureSourceApplier(PackOutput output, ExistingFileHelper fileHelper){
		super(output, fileHelper, ImmersiveCookFarm.MODID);
	}
	
	@Override
	protected void addSources(){
		final SourceList blockAtlas = atlas(SpriteSourceProvider.BLOCKS_ATLAS);
		
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/grill_oven"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/cookpot"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/multiblock_base"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(Resource.mod("block/multiblock/multiblock_components"), Optional.empty()));
	}
}
