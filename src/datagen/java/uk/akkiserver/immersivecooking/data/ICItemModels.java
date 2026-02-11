package uk.akkiserver.immersivecooking.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import uk.akkiserver.immersivecooking.common.ICContent;
import uk.akkiserver.immersivecooking.data.blockstates.ICBlockStates;
import uk.akkiserver.immersivecooking.data.models.TRSRItemModelProvider;
import uk.akkiserver.immersivecooking.data.models.TRSRModelBuilder;
import uk.akkiserver.immersivecooking.utils.Resource;

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
