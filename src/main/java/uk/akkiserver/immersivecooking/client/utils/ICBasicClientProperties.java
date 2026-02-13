package uk.akkiserver.immersivecooking.client.utils;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
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
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.ICTemplateMultiblock;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class ICBasicClientProperties implements MultiblockManualData {
    private final ICTemplateMultiblock multiblock;
    @Nullable
    private NonNullList<ItemStack> materials;
    private final ResourceLocation modelLocation;
    private final Optional<Quaternionf> rotation;

    public ICBasicClientProperties(ICTemplateMultiblock multiblock) {
        this(multiblock, OptionalDouble.empty());
    }

    public ICBasicClientProperties(ICTemplateMultiblock multiblock, OptionalDouble yRotationRadians) {
        this.multiblock = multiblock;
        // Use the same path convention as ClientSetup registration
        this.modelLocation = ResourceLocation.fromNamespaceAndPath(multiblock.getBlockName().getNamespace(),
                "block/multiblock/" + multiblock.getBlockName().getPath() + ".obj");
        this.rotation = yRotationRadians.stream()
                .mapToObj(r -> new Quaternionf().rotateY((float) r))
                .findAny();
    }

    public static void initModels() {
        // No-op
    }

    @Override
    public NonNullList<ItemStack> getTotalMaterials() {
        if (materials == null) {
            List<StructureBlockInfo> structure = multiblock.getStructure(Minecraft.getInstance().level);
            materials = NonNullList.create();
            for (StructureBlockInfo info : structure) {
                if (info.state().hasProperty(IEProperties.MULTIBLOCKSLAVE)
                        && info.state().getValue(IEProperties.MULTIBLOCKSLAVE))
                    continue;
                ItemStack picked = Utils.getPickBlock(info.state());
                boolean added = false;
                for (ItemStack existing : materials)
                    if (ItemStack.isSameItem(existing, picked)) {
                        existing.grow(1);
                        added = true;
                        break;
                    }
                if (!added)
                    materials.add(picked.copy());
            }
        }
        return materials;
    }

    @Override
    public void renderFormedStructure(PoseStack transform, MultiBufferSource bufferSource) {
        transform.pushPose();

        BlockPos offset = multiblock.getMasterFromOriginOffset();
        transform.translate(offset.getX(), offset.getY(), offset.getZ());

        if (rotation.isPresent()) {
            transform.translate(0.5, 0, 0.5);
            transform.mulPose(rotation.get());
            transform.translate(-0.5, 0, -0.5);
        }

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
        if (model != null && model != Minecraft.getInstance().getModelManager().getMissingModel()) {
            List<BakedQuad> quads = model.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null);
            VertexConsumer buffer = bufferSource.getBuffer(IERenderTypes.TRANSLUCENT_FULLBRIGHT);
            quads.forEach(quad -> buffer.putBulkData(
                    transform.last(), quad, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY));
        }

        transform.popPose();
    }

    @Override
    public boolean canRenderFormedStructure() {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
        return model != null && model != Minecraft.getInstance().getModelManager().getMissingModel();
    }
}
