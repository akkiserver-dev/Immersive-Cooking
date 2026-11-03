package uk.akkiserver.immersivecooking.client.model;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Corrects IE's split-model offset for an even-width machine with an off-center master. */
public class SlicerModel extends BakedModelWrapper<BakedModel> {
    public SlicerModel(BakedModel model) {
        super(model);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData tileData) {
        ModelData data = super.getModelData(level, pos, state, tileData);
        if (!(level.getBlockEntity(pos) instanceof IMultiblockBE<?> multiblock)) return data;
        var helper = multiblock.getHelper();
        BlockPos offset = helper.getPositionInMB().subtract(helper.getMultiblock().masterPosInMB());
        if (state.getValue(IEProperties.MIRRORED))
            offset = new BlockPos(-offset.getX(), offset.getY(), offset.getZ());
        return data.derive().with(IEProperties.Model.SUBMODEL_OFFSET, offset).build();
    }
}
