package uk.akkiserver.immersivecooking.client.utils;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.common.util.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.ICTemplateMultiblock;

import javax.annotation.Nullable;
import java.util.List;

public class ICMultiblockProperties implements ClientMultiblocks.MultiblockManualData {
    private static final Direction[] DIRECTIONS;
    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    static {
        Direction[] source = Direction.values();
        DIRECTIONS = new Direction[source.length + 1];
        System.arraycopy(source, 0, DIRECTIONS, 0, source.length);
        DIRECTIONS[DIRECTIONS.length - 1] = null;
    }

    protected final ICTemplateMultiblock multiblock;
    protected final ItemStack renderStack;
    protected final Vec3 renderOffset;

    @Nullable
    private NonNullList<ItemStack> materials;

    public ICMultiblockProperties(ICTemplateMultiblock multiblock, double offX, double offY, double offZ) {
        this.multiblock = multiblock;
        this.renderStack = new ItemStack(multiblock.getBlock());
        this.renderOffset = new Vec3(offX, offY, offZ);
    }

    @Override
    public void renderFormedStructure(PoseStack matrix, MultiBufferSource bufferSource) {
        matrix.pushPose();

        BlockPos blockOffset = multiblock.getMasterFromOriginOffset();
        matrix.translate(blockOffset.getX(), blockOffset.getY(), blockOffset.getZ());

        matrix.translate(renderOffset.x, renderOffset.y, renderOffset.z);

        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(this.renderStack, null, null, 0);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(IERenderTypes.TRANSLUCENT_FULLBRIGHT);
        PoseStack.Pose lastPose = matrix.last();

        for (Direction direction : DIRECTIONS) {
            RANDOM_SOURCE.setSeed(42L);
            List<BakedQuad> quads = bakedModel.getQuads(null, direction, RANDOM_SOURCE, ModelData.EMPTY, null);
            for (BakedQuad quad : quads) {
                vertexConsumer.putBulkData(
                        lastPose, quad, 1.0F, 1.0F, 1.0F,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY
                );
            }
        }
        matrix.popPose();
    }

    @Override
    public NonNullList<ItemStack> getTotalMaterials() {
        if (materials == null) {
            List<StructureBlockInfo> structure = multiblock.getStructure(Minecraft.getInstance().level);
            materials = NonNullList.create();
            for (StructureBlockInfo info : structure) {
                if (info.state().hasProperty(IEProperties.MULTIBLOCKSLAVE) && info.state().getValue(IEProperties.MULTIBLOCKSLAVE)) {
                    continue;
                }

                ItemStack picked = Utils.getPickBlock(info.state());
                boolean added = false;
                for (ItemStack existing : materials) {
                    if (ItemStack.isSameItem(existing, picked)) {
                        existing.grow(1);
                        added = true;
                        break;
                    }
                }
                if (!added) materials.add(picked.copy());
            }
        }
        return materials;
    }

    @Override
    public boolean canRenderFormedStructure() {
        return true;
    }
}