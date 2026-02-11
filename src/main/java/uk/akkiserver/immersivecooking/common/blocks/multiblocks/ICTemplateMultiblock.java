package uk.akkiserver.immersivecooking.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public abstract class ICTemplateMultiblock extends IETemplateMultiblock {
    public ICTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin,
            BlockPos size, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
    }
}
