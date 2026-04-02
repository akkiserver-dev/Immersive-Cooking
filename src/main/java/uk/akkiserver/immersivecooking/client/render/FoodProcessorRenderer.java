package uk.akkiserver.immersivecooking.client.render;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.logic.FoodProcessorLogic.State;

public class FoodProcessorRenderer extends IEBlockEntityRenderer<MultiblockBlockEntityMaster<State>> {
    public static DynamicModel MIXER;
    public static DynamicModel CUTTER;

    @Override
    public void render(MultiblockBlockEntityMaster<State> te, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        final IMultiblockBEHelperMaster<State> helper = te.getHelper();
        final State state = helper.getState();
        final MultiblockOrientation orientation = helper.getContext().getLevel().getOrientation();
        final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        int catalyst = state.getCatalyst();
        if (catalyst == 0) return;

        matrixStack.pushPose();

        bufferIn = BERenderUtils.mirror(orientation, matrixStack, bufferIn);

        matrixStack.pushPose();
        {
            final Direction front = orientation.front();
            matrixStack.translate(
                    front == Direction.SOUTH || front == Direction.WEST ? 0.0 : 1.0,
                    0.5,
                    front == Direction.SOUTH || front == Direction.EAST ? 1.0 : 0.0
            );

            float rotation = 0;
            if (state.active) {
                long time = te.getLevel().getGameTime();
                rotation = (time + partialTicks) * 16.0f;
            }

            matrixStack.mulPose(new Quaternionf().rotateY(rotation * Mth.DEG_TO_RAD));

            matrixStack.translate(-0.5, -0.5, -0.5);

            DynamicModel modelToRender = (catalyst == 1) ? MIXER : CUTTER;

            if (modelToRender != null && modelToRender.get() != null) {
                blockRenderer.getModelRenderer().renderModel(
                        matrixStack.last(), bufferIn.getBuffer(RenderType.solid()), null, modelToRender.get(),
                        1.0f, 1.0f, 1.0f,
                        combinedLightIn, combinedOverlayIn, ModelData.EMPTY, RenderType.solid()
                );
            }
        }
        matrixStack.popPose();

        matrixStack.popPose();
    }
}