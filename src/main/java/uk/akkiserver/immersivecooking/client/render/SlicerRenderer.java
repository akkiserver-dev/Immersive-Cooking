package uk.akkiserver.immersivecooking.client.render;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.client.render.tile.IEMultiblockRenderer;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import uk.akkiserver.immersivecooking.common.block.multiblock.logic.SlicerLogic.State;

public class SlicerRenderer extends IEMultiblockRenderer<State> {
    @Override
    public void render(IMultiblockContext<State> context, float partialTicks, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {
        State state = context.getState();
        var level = context.getLevel().getRawLevel();
        var orientation = context.getLevel().getOrientation();
        var minecraft = Minecraft.getInstance();
        pose.pushPose();
        pose.translate(.5, 0, .5);
        buffers = BERenderUtils.mirror(orientation, pose, buffers);
        pose.mulPose(Axis.YP.rotationDegrees(180 - orientation.front().toYRot()));

        for (var process : state.processor.getQueue()) {
            if (!(process instanceof MultiblockProcessInWorld<?> inWorld)) continue;
            var displays = inWorld.getDisplayItem(level);
            if (displays.isEmpty()) continue;
            float progress = Mth.clamp((process.processTick + (state.active ? partialTicks : 0))
                    / process.getMaxTicks(level), 0, 1);
            for (int i = 0; i < displays.size(); i++) {
                var display = displays.get(i);
                if (display.isEmpty()) continue;
                // Each output stack is rendered once, with successive byproducts slightly offset.
                double xOffset = .05 * i;
                double zOffset = .06 * i;
                double itemHeight = (minecraft.getItemRenderer().getModel(display, level, null, 0).isGui3d()
                        ? .375 : .16) + .003 * i;
                pose.pushPose();
                // The master is at (0, 1, 1). Items follow the template's north-facing belt.
                pose.translate(1 + xOffset, itemHeight, 1.25 - 2.5 * progress + zOffset);
                pose.mulPose(Axis.XP.rotationDegrees(-90));
                pose.scale(.625f, .625f, 1);
                Vec3 itemPosition = context.getLevel().toAbsolute(
                        new Vec3(1.5 + xOffset, 1 + itemHeight, 2.75 - 2.5 * progress + zOffset));
                int itemLight = LevelRenderer.getLightColor(level, BlockPos.containing(itemPosition));
                minecraft.getItemRenderer().renderStatic(display, ItemDisplayContext.FIXED,
                        itemLight, overlay, pose, buffers, level, 0);
                pose.popPose();
            }
        }
        pose.popPose();
    }
}
