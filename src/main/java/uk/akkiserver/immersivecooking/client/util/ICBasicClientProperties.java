package uk.akkiserver.immersivecooking.client.util;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import uk.akkiserver.immersivecooking.common.block.multiblock.ICTemplateMultiblock;

import java.util.*;

public class ICBasicClientProperties implements ClientMultiblocks.MultiblockManualData {
    private final ICTemplateMultiblock multiblock;
    @Nullable
    private NonNullList<ItemStack> materials;
    private final ItemStack renderStack;
    @Nullable
    private final Vec3 renderOffset;
    private final float scale;

    public ICBasicClientProperties(ICTemplateMultiblock multiblock) {
        this(multiblock, null, 1);
    }

    public ICBasicClientProperties(ICTemplateMultiblock multiblock, double offX, double offY, double offZ) {
        this(multiblock, new Vec3(offX, offY, offZ), 1);
    }

    public ICBasicClientProperties(ICTemplateMultiblock multiblock, double offX, double offY, double offZ, float scale) {
        this(multiblock, new Vec3(offX, offY, offZ), scale);
    }

    private ICBasicClientProperties(ICTemplateMultiblock multiblock, @Nullable Vec3 renderOffset, float scale) {
        this.multiblock = multiblock;
        this.renderStack = new ItemStack(multiblock.getBlock());
        this.renderOffset = renderOffset;
        this.scale = scale;
    }

    /**
     * Skipping normal rendering behaviour
     */
    protected boolean usingCustomRendering() {
        return false;
    }

    @Override
    public NonNullList<ItemStack> getTotalMaterials(){
        if(this.materials != null)
            return this.materials;

        final List<StructureTemplate.StructureBlockInfo> structure = this.multiblock.getStructure(Minecraft.getInstance().level);
        this.materials = structure.stream()
                .map(info -> new ItemStack(info.state().getBlock().asItem(), 1))
                .collect(HashMap<Item, Integer>::new, (map, stack) -> map.compute(stack.getItem(), (item, count) -> count == null ? 1 : count + 1), HashMap::putAll)
                .entrySet().stream()
                .map(e -> new ItemStack(e.getKey(), e.getValue()))
                .collect(NonNullList::create, AbstractList::add, AbstractCollection::addAll);

        return this.materials;
    }

    @Override
    public boolean canRenderFormedStructure() {
        return this.renderOffset != null;
    }

    /**
     * Allowing custom accessories to be rendered. Unused if {@link #usingCustomRendering()} returns true
     */
    public void renderExtras(PoseStack matrix, MultiBufferSource buffer) {
    }

    /**
     * Only used when {@link #usingCustomRendering()} returns true
     */
    public void renderCustomFormedStructure(PoseStack matrix, MultiBufferSource buffer) {
    }

    @Override
    public final void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer) {
        Objects.requireNonNull(this.renderOffset);

        if (usingCustomRendering()) {
            renderCustomFormedStructure(matrix, buffer);
            return;
        }

        matrix.translate(this.renderOffset.x, this.renderOffset.y, this.renderOffset.z);
        matrix.scale(this.scale, this.scale, this.scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(this.renderStack, ItemDisplayContext.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, matrix, buffer, Minecraft.getInstance().level, 0);
        matrix.pushPose();
        {
            renderExtras(matrix, buffer);
        }
        matrix.popPose();
    }
}
