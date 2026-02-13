package uk.akkiserver.immersivecooking.common.blocks.multiblocks;

import net.minecraft.core.BlockPos;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.utils.Resource;

public class FoodFermenterMultiblock extends ICTemplateMultiblock {
    public static final CookpotMultiblock INSTANCE = new CookpotMultiblock();

    public FoodFermenterMultiblock() {
        super(Resource.mod("multiblocks/food_fermenter"),
                new BlockPos(1, 1, 1), new BlockPos(1, 1, 1), new BlockPos(3, 3, 3),
                ICContent.Multiblock.FOOD_FERMENTER);
    }

    @Override
    public float getManualScale() {
        return 12;
    }
}
