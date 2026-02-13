package uk.akkiserver.immersivecooking.data;

import com.google.common.base.Preconditions;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.common.fluids.ICFluids;
import uk.akkiserver.immersivecooking.data.blockstates.ICBlockStates;
import uk.akkiserver.immersivecooking.data.models.TRSRItemModelProvider;
import uk.akkiserver.immersivecooking.data.models.TRSRModelBuilder;
import uk.akkiserver.immersivecooking.common.utils.Resource;

public class ICItemModels extends TRSRItemModelProvider {
    private final ICBlockStates blockStates;

    public ICItemModels(PackOutput output, ExistingFileHelper existing, ICBlockStates blockStates) {
        super(output, existing);
        this.blockStates = blockStates;
    }

    @Override
    protected void registerModels() {
        getBuilder(ICContent.Multiblock.GRILL_OVEN.blockItem().get())
                .parent(blockStates.grillOvenOn)
                .transforms(Resource.ie("item/blastfurnace"));

        for (ICFluids.FluidEntry entry : ICFluids.ALL_ENTRIES) {
            String fileName = name(entry.getBucket());

            String texturePath = fileName;
            if (fileName.contains("grapejuice")) {
                texturePath = fileName.contains("red") ? "red_grapejuice_bucket" : "white_grapejuice_bucket";
            }

            getBuilder(fileName)
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", mcLoc("item/bucket"))
                    .texture("layer1", Resource.mod("item/" + texturePath));
        }

        obj(ICContent.Multiblock.COOKPOT.blockItem().get(), Resource.mod("block/multiblock/cookpot.obj")).transforms(Resource.mod("item/multiblock"));
    }

    private TRSRModelBuilder obj(ItemLike item, ResourceLocation model) {
        Preconditions.checkArgument(existingFileHelper.exists(model, PackType.CLIENT_RESOURCES, "", "models"));
        return getBuilder(item)
                .customLoader(ObjModelBuilder::begin)
                .flipV(true)
                .modelLocation(ResourceLocation.fromNamespaceAndPath(model.getNamespace(), "models/" + model.getPath()))
                .end();
    }

    private TRSRModelBuilder getBuilder(ItemLike item) {
        return getBuilder(name(item));
    }

    private String name(ItemLike item) {
        return ForgeRegistries.ITEMS.getKey(item.asItem()).getPath();
    }

    @Override
    public @NotNull String getName() {
        return "Item models";
    }
}
