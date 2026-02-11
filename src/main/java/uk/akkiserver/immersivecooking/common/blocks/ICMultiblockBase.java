package uk.akkiserver.immersivecooking.common.blocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nonnull;

public class ICMultiblockBase<T extends IMultiblockState> extends MultiblockPartBlock<T> {
    private final MultiblockRegistration<T> multiblock;

    public ICMultiblockBase(BlockBehaviour.Properties properties, MultiblockRegistration<T> multiblock){
        super(properties, multiblock);
        this.multiblock = multiblock;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder){
        super.createBlockStateDefinition(builder);
        builder.add(IEProperties.MIRRORED);
        builder.add(IEProperties.ACTIVE);
    }
}
