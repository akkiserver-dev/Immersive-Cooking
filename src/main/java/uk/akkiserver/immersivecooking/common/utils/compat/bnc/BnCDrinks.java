package uk.akkiserver.immersivecooking.common.utils.compat.bnc;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import umpaz.brewinandchewin.common.registry.BnCFluids;
import umpaz.brewinandchewin.common.registry.BnCItems;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public enum BnCDrinks {
    BEER                (BnCItems.BEER,                BnCFluids.BEER,                250),
    VODKA               (BnCItems.VODKA,               BnCFluids.VODKA,               250),
    MEAD                (BnCItems.MEAD,                BnCFluids.MEAD,                250),
    RICE_WINE           (BnCItems.RICE_WINE,           BnCFluids.RICE_WINE,           250),
    PALE_JANE           (BnCItems.PALE_JANE,           BnCFluids.PALE_JANE,           250),
    EGG_GROG            (BnCItems.EGG_GROG,            BnCFluids.EGG_GROG,            250),
    GLITTERING_GRENADINE(BnCItems.GLITTERING_GRENADINE,BnCFluids.GLITTERING_GRENADINE,250),
    SACCHARINE_RUM      (BnCItems.SACCHARINE_RUM,      BnCFluids.SACCHARINE_RUM,      250),
    SALTY_FOLLY         (BnCItems.SALTY_FOLLY,         BnCFluids.SALTY_FOLLY,         250),
    BLOODY_MARY         (BnCItems.BLOODY_MARY,         BnCFluids.BLOODY_MARY,         250),
    RED_RUM             (BnCItems.RED_RUM,             BnCFluids.RED_RUM,             250),
    STRONGROOT_ALE      (BnCItems.STRONGROOT_ALE,      BnCFluids.STRONGROOT_ALE,      250),
    STEEL_TOE_STOUT     (BnCItems.STEEL_TOE_STOUT,     BnCFluids.STEEL_TOE_STOUT,     250),
    DREAD_NOG           (BnCItems.DREAD_NOG,           BnCFluids.DREAD_NOG,           250),
    WITHERING_DROSS     (BnCItems.WITHERING_DROSS,     BnCFluids.WITHERING_DROSS,     250),
    KOMBUCHA            (BnCItems.KOMBUCHA,            BnCFluids.KOMBUCHA,            250);

    private final Supplier<Item> itemSupplier;
    private final Supplier<? extends Fluid> fluidSupplier;
    private final int amount;

    BnCDrinks(Supplier<Item> item, Supplier<? extends Fluid> fluid, int amount) {
        this.itemSupplier = item;
        this.fluidSupplier = fluid;
        this.amount = amount;
    }

    public Item getItem()   { return itemSupplier.get(); }
    public Fluid getFluid() { return fluidSupplier.get(); }
    public int getAmount()  { return amount; }

    public FluidStack toFluidStack() {
        return new FluidStack(getFluid(), amount);
    }

    public static Optional<BnCDrinks> fromItem(Item item) {
        return Arrays.stream(values()).filter(e -> e.getItem() == item).findFirst();
    }

    public static Optional<BnCDrinks> fromFluid(Fluid fluid) {
        return Arrays.stream(values()).filter(e -> e.getFluid() == fluid).findFirst();
    }
}