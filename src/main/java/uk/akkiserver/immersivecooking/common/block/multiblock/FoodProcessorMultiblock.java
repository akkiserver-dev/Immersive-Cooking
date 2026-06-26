package uk.akkiserver.immersivecooking.common.block.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecooking.client.util.ICBasicClientProperties;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.util.Resource;

import java.util.function.Consumer;

public class FoodProcessorMultiblock extends ICTemplateMultiblock {
    public static final FoodProcessorMultiblock INSTANCE = new FoodProcessorMultiblock();

    public FoodProcessorMultiblock() {
        super(Resource.mod("multiblocks/food_processor"),
                new BlockPos(1, 1, 1), new BlockPos(1, 1, 1), new BlockPos(3, 3, 3),
                ICContent.Multiblock.FOOD_PROCESSOR);
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
