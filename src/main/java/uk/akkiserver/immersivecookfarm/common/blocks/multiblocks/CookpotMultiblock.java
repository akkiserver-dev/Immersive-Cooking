package uk.akkiserver.immersivecookfarm.common.blocks.multiblocks;

import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecookfarm.common.ICContent;
import uk.akkiserver.immersivecookfarm.common.utils.Resource;

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
}
