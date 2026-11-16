package uk.akkiserver.immersivecooking.common.block.multiblock;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher.Result;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.util.Resource;

import java.util.List;

/** Structure and static model prototype, without processing behavior. */
public class TunnelOvenMultiblock extends ICTemplateMultiblock {
    public static final TunnelOvenMultiblock INSTANCE = new TunnelOvenMultiblock();

    public TunnelOvenMultiblock() {
        super(Resource.mod("multiblocks/tunnel_oven"),
                new BlockPos(1, 1, 2), new BlockPos(2, 1, 4), new BlockPos(3, 2, 5),
                ICContent.Multiblock.TUNNEL_OVEN,
                List.of((expected, found, world, pos) -> {
                    var heater = IEBlocks.MetalDevices.FURNACE_HEATER.get();
                    if (expected.is(heater) && found.is(heater)
                            && found.setValue(IEProperties.FACING_ALL, expected.getValue(IEProperties.FACING_ALL)) == expected) {
                        return Result.allow(2);
                    }
                    return Result.DEFAULT;
                }));
    }

    @Override
    public float getManualScale() {
        return 10;
    }
}
