package uk.akkiserver.immersivecooking.data.blockstates;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.data.DataGenUtils;
import blusunrize.immersiveengineering.data.models.MirroredModelBuilder;
import blusunrize.immersiveengineering.data.models.ModelProviderUtils;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.NongeneratedModels.NongeneratedModel;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.ImmersiveCooking;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.CookpotMultiblock;
import uk.akkiserver.immersivecooking.common.blocks.multiblocks.ICTemplateMultiblock;
import uk.akkiserver.immersivecooking.common.utils.Resource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ICBlockStates extends BlockStateProvider {
    private static final List<Vec3i> CUBE_THREE = BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
            .map(BlockPos::immutable)
            .collect(Collectors.toList());
    private static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();

    protected final ExistingFileHelper existingFileHelper;
    protected final NongeneratedModels innerModels;

    public ModelFile grillOvenOff;
    public ModelFile grillOvenOn;
    public final Map<Block, ModelFile> unsplitModels = new HashMap<>();

    public ICBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ImmersiveCooking.MODID, exFileHelper);
        this.existingFileHelper = exFileHelper;
        this.innerModels = new NongeneratedModels(output, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        grillOvenOff = cubeThree(
                "grill_oven_off",
                Resource.mod("block/multiblock/grill_oven"),
                Resource.mod("block/multiblock/grill_oven_off")
        );
        grillOvenOn = cubeThree(
                "grill_oven_on",
                Resource.mod("block/multiblock/grill_oven"),
                Resource.mod("block/multiblock/grill_oven_on")
        );

        createMultiblock(
                ICContent.Multiblock.GRILL_OVEN.block(),
                grillOvenOff,
                grillOvenOn,
                IEProperties.ACTIVE
        );

        createMultiblock(innerObj("block/multiblock/cookpot.obj"), CookpotMultiblock.INSTANCE);
    }

    protected NongeneratedModel innerObj(String loc, @Nullable RenderType layer) {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        final var result = obj(loc.substring(0, loc.length() - 4), modLoc(loc), innerModels);
        setRenderType(layer, result);
        return result;
    }

    protected NongeneratedModel innerObj(String loc) {
        return innerObj(loc, null);
    }

    protected void setRenderType(@Nullable RenderType type, ModelBuilder<?>... builders) {
        if (type != null) {
            final String typeName = ModelProviderUtils.getName(type);
            for(final ModelBuilder<?> model : builders) {
                model.renderType(typeName);
            }
        }
    }

    protected BlockModelBuilder obj(String loc) {
        return obj(loc, (RenderType)null);
    }

    protected BlockModelBuilder obj(String loc, @Nullable RenderType layer) {
        final var model = obj(loc, models());
        setRenderType(layer, model);
        return model;
    }

    protected <T extends ModelBuilder<T>> T obj(String loc, ModelProvider<T> modelProvider) {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        return obj(loc.substring(0, loc.length()-4), modLoc(loc), modelProvider);
    }

    protected <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, ModelProvider<T> provider) {
        return obj(name, model, ImmutableMap.of(), provider);
    }

    protected <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider) {
        return obj(provider.withExistingParent(name, mcLoc("block")), model, textures);
    }

    protected <T extends ModelBuilder<T>> T obj(T base, ResourceLocation model, Map<String, ResourceLocation> textures) {
        assertModelExists(model);
        T ret = base
                .customLoader(ObjModelBuilder::begin)
                .automaticCulling(false)
                .modelLocation(addModelsPrefix(model))
                .flipV(true)
                .end();

        String particleTex = DataGenUtils.getTextureFromObj(model, existingFileHelper);
        if (particleTex.charAt(0) == '#') {
            particleTex = textures.get(particleTex.substring(1)).toString();
        }
        ret.texture("particle", particleTex);
        generatedParticleTextures.put(ret.getLocation(), particleTex);
        for(Map.Entry<String, ResourceLocation> e : textures.entrySet()) {
            ret.texture(e.getKey(), e.getValue());
        }
        return ret;
    }

    public void assertModelExists(ResourceLocation name) {
        String suffix = name.getPath().contains(".") ? "" : ".json";
        Preconditions.checkState(
                existingFileHelper.exists(name, PackType.CLIENT_RESOURCES, suffix, "models"),
                "Model \"" + name + "\" does not exist");
    }

    protected ResourceLocation addModelsPrefix(ResourceLocation in) {
        return ResourceLocation.fromNamespaceAndPath(in.getNamespace(), "models/" + in.getPath());
    }

    private ModelFile cubeThree(String name, ResourceLocation sideTexture, ResourceLocation frontTexture) {
        ResourceLocation objLoc = ResourceLocation.fromNamespaceAndPath("immersiveengineering", "block/stone_multiblocks/cube_three.obj");

        NongeneratedModel baseModel = obj(
                name,
                objLoc,
                ImmutableMap.of("side", sideTexture, "front", frontTexture),
                innerModels
        );
        return splitModel(name + "_split", baseModel, CUBE_THREE, false);
    }

    private void createMultiblock(NongeneratedModel unsplitModel, ICTemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, false);
    }

    private void createDynamicMultiblock(NongeneratedModel unsplitModel, ICTemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, true);
    }

    private void createMultiblock(NongeneratedModel unsplitModel, ICTemplateMultiblock multiblock, boolean dynamic) {
        final ModelFile mainModel = split(unsplitModel, multiblock, false, dynamic);
        if (multiblock.getBlock().getStateDefinition().getProperties().contains(IEProperties.MIRRORED)) {
            createMultiblock(
                    multiblock::getBlock,
                    mainModel,
                    split(mirror(unsplitModel, innerModels), multiblock, true, dynamic),
                    IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED
            );
        } else {
            createMultiblock(multiblock::getBlock, mainModel, null, IEProperties.FACING_HORIZONTAL, null);
        }
    }

    protected <T extends ModelBuilder<T>> T mirror(NongeneratedModel inner, ModelProvider<T> provider) {
        return provider.getBuilder(inner.getLocation().getPath() + "_mirrored")
                .customLoader(MirroredModelBuilder::begin)
                .inner(inner)
                .end();
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel) {
        createMultiblock(b, masterModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel, @Nullable Property<Boolean> mirroredState) {
        createMultiblock(b, masterModel, mirroredModel, IEProperties.FACING_HORIZONTAL, mirroredState);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState) {
        unsplitModels.put(b.get(), masterModel);
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b.get());
        boolean[] possibleMirrorStates;
        if (mirroredState != null) {
            possibleMirrorStates = new boolean[]{false, true};
        } else {
            possibleMirrorStates = new boolean[1];
        }
        for (boolean mirrored : possibleMirrorStates) {
            for (Direction dir : facing.getPossibleValues()) {
                final int angleY;
                final int angleX;
                if (facing.getPossibleValues().contains(Direction.UP)) {
                    angleX = -90 * dir.getStepY();
                    if (dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, 180);
                    else
                        angleY = 0;
                } else {
                    angleY = getAngle(dir, 180);
                    angleX = 0;
                }
                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState().with(facing, dir);
                if (mirroredState != null) {
                    partialState = partialState.with(mirroredState, mirrored);
                }
                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
        }
    }

    protected BlockModelBuilder splitModel(String name, NongeneratedModel model, List<Vec3i> parts, boolean dynamic) {
        BlockModelBuilder result = models().withExistingParent(name, mcLoc("block"))
                .customLoader(SplitModelBuilder::begin)
                .innerModel(model)
                .parts(parts)
                .dynamic(dynamic)
                .end();
        addParticleTextureFrom(result, model);
        return result;
    }

    protected void addParticleTextureFrom(BlockModelBuilder result, ModelFile model) {
        String particles = generatedParticleTextures.get(model.getLocation());
        if (particles != null) {
            result.texture("particle", particles);
            generatedParticleTextures.put(result.getLocation(), particles);
        }
    }

    private ModelFile split(NongeneratedModel loc, TemplateMultiblock mb) {
        return split(loc, mb, false);
    }

    private ModelFile split(NongeneratedModel loc, TemplateMultiblock mb, boolean mirror) {
        return split(loc, mb, mirror, false);
    }

    private ModelFile split(NongeneratedModel loc, TemplateMultiblock mb, boolean mirror, boolean dynamic) {
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if (mirror) {
            loadTemplateFor(mb);
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX()-p.getX()-1, p.getY(), p.getZ());
        }
        return split(loc, mb, transform, dynamic);
    }

    private ModelFile split(NongeneratedModel name, TemplateMultiblock multiblock, UnaryOperator<BlockPos> transform, boolean dynamic) {
        loadTemplateFor(multiblock);
        final Vec3i offset = multiblock.getMasterFromOriginOffset();
        Stream<Vec3i> partsStream = multiblock.getTemplate(null).blocksWithoutAir()
                .stream()
                .map(StructureTemplate.StructureBlockInfo::pos)
                .map(transform)
                .map(p -> p.subtract(offset));
        return split(name, partsStream.collect(Collectors.toList()), dynamic);
    }

    protected ModelFile split(NongeneratedModel baseModel, List<Vec3i> parts, boolean dynamic) {
        return splitModel(baseModel.getLocation().getPath() + "_split", baseModel, parts, dynamic);
    }

    protected ModelFile split(NongeneratedModel baseModel, List<Vec3i> parts) {
        return split(baseModel, parts, false);
    }

    protected ModelFile splitDynamic(NongeneratedModel baseModel, List<Vec3i> parts) {
        return split(baseModel, parts, true);
    }

    protected int getAngle(Direction dir, int offset) {
        return (int) ((dir.toYRot() + offset) % 360);
    }

    private void loadTemplateFor(TemplateMultiblock mb) {
        final ResourceLocation name = mb.getUniqueName();
        if (TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.containsKey(name)) return;

        final String filePath = "structures/" + name.getPath() + ".nbt";
        int slash = filePath.indexOf('/');
        String prefix = filePath.substring(0, slash);
        ResourceLocation shortLoc = ResourceLocation.fromNamespaceAndPath(
                name.getNamespace(), filePath.substring(slash + 1)
        );

        try {
            final net.minecraft.server.packs.resources.Resource resource =
                    existingFileHelper.getResource(shortLoc, PackType.SERVER_DATA, "", prefix);
            try (final InputStream input = resource.open()) {
                final CompoundTag nbt = NbtIo.readCompressed(input);
                final StructureTemplate template = new StructureTemplate();
                template.load(BuiltInRegistries.BLOCK.asLookup(), nbt);
                TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(name, template);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template for " + name, e);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Immersive Cooking Blockstates";
    }
}