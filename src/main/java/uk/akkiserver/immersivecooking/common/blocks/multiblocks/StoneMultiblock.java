package uk.akkiserver.immersivecooking.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public abstract class StoneMultiblock extends ICTemplateMultiblock {
    public StoneMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size,
            MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
    }

    @Override
    public boolean canBeMirrored() {
        return false;
    }
}
