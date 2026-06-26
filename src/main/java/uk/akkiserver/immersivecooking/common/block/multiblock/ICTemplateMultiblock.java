package uk.akkiserver.immersivecooking.common.block.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import uk.akkiserver.immersivecooking.client.util.ICBasicClientProperties;

import java.util.function.Consumer;

public abstract class ICTemplateMultiblock extends IETemplateMultiblock {
    public ICTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin,
            BlockPos size, MultiblockRegistration<?> logic) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
    }

    @Override
    public void initializeClient(Consumer<MultiblockManualData> consumer) {
        consumer.accept(new ICBasicClientProperties(this));
    }
}
