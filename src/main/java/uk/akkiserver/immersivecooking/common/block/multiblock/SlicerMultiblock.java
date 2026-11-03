package uk.akkiserver.immersivecooking.common.block.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.client.util.ICBasicClientProperties;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.block.multiblock.logic.SlicerLogic;
import uk.akkiserver.immersivecooking.common.util.Resource;

import java.util.function.Consumer;

public class SlicerMultiblock extends ICTemplateMultiblock {
    public static final SlicerMultiblock INSTANCE = new SlicerMultiblock();

    public SlicerMultiblock() {
        super(Resource.mod("multiblocks/slicer"),
                new BlockPos(0, 1, 1), SlicerLogic.REDSTONE_POS, new BlockPos(2, 2, 3),
                ICContent.Multiblock.SLICER);
    }

    @Override
    public boolean createStructure(Level world, BlockPos pos, Direction side, @Nullable Player player) {
        if (!side.getAxis().isHorizontal()) return false;
        // The RS block is exposed on the X side, while IE interprets the clicked
        // face as the structure front. Try both rotations for mirrored layouts.
        return super.createStructure(world, pos, side.getClockWise(), player)
                || super.createStructure(world, pos, side.getCounterClockWise(), player);
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
