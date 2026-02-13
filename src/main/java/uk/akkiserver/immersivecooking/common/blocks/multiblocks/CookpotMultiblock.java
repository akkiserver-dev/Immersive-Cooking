package uk.akkiserver.immersivecooking.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecooking.client.utils.ICBasicClientProperties;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import java.util.function.Consumer;

public class CookpotMultiblock extends ICTemplateMultiblock {
    public static final CookpotMultiblock INSTANCE = new CookpotMultiblock();

    public CookpotMultiblock() {
            super(Resource.mod("multiblocks/cookpot"),
                    new BlockPos(1, 1, 1), new BlockPos(1, 1, 1), new BlockPos(3, 3, 3),
                    ICContent.Multiblock.COOKPOT);
    }

    @Override
    public float getManualScale() {
        return 12;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> data) {
        data.accept(new ICBasicClientProperties(this, 1.5, 1.5, 1.5));
    }
}
