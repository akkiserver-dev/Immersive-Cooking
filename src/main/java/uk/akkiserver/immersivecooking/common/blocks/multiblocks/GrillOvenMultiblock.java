package uk.akkiserver.immersivecooking.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecooking.client.utils.ICBasicClientProperties;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.GrillOvenLogic;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.function.Consumer;

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


    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> data) {
        data.accept(new ICBasicClientProperties(this, 0.5, 0.5, 0.5));
    }
}
