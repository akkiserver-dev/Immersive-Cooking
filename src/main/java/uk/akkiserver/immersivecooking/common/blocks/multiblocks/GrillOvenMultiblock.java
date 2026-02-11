package uk.akkiserver.immersivecooking.common.blocks.multiblocks;

import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic;
import uk.akkiserver.immersivecooking.utils.Resource;

public class GrillOvenMultiblock extends StoneMultiblock {
    public static final GrillOvenMultiblock INSTANCE = new GrillOvenMultiblock();

    public GrillOvenMultiblock() {
        super(Resource.mod("multiblocks/grill_oven"),
                GrillOvenLogic.MASTER_OFFSET, new BlockPos(1, 1, 2), new BlockPos(3, 3, 3),
                ICContent.Multiblock.GRILL_OVEN);
    }

    @Override
    public float getManualScale() {
        return 16;
    }
}
