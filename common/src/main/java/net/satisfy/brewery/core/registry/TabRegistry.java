package net.satisfy.brewery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.brewery.Brewery;

@SuppressWarnings("unused")
public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Brewery.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> BREWERY_TAB = CREATIVE_MODE_TABS.register("brewery", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
            .icon(() -> new ItemStack(ObjectRegistry.BEER_WHEAT.get()))
            .title(Component.translatable("itemGroup.brewery.creative_tab"))
            .displayItems((parameters, out) -> {
                out.accept(ObjectRegistry.CABINET.get());
                out.accept(ObjectRegistry.DRAWER.get());
                out.accept(ObjectRegistry.BAR_COUNTER.get());
                out.accept(ObjectRegistry.SIDEBOARD.get());
                out.accept(ObjectRegistry.WALL_CABINET.get());
                out.accept(ObjectRegistry.TABLE.get());
                out.accept(ObjectRegistry.BENCH.get());
                out.accept(ObjectRegistry.PATTERNED_WOOL.get());
                out.accept(ObjectRegistry.PATTERNED_CARPET.get());
                out.accept(ObjectRegistry.HOPS.get());
                out.accept(ObjectRegistry.DRIED_WHEAT.get());
                out.accept(ObjectRegistry.DRIED_BARLEY.get());
                out.accept(ObjectRegistry.DRIED_CORN.get());
                out.accept(ObjectRegistry.DRIED_OAT.get());
                out.accept(ObjectRegistry.WOODEN_BREWINGSTATION.get());
                out.accept(ObjectRegistry.COPPER_BREWINGSTATION.get());
                out.accept(ObjectRegistry.NETHERITE_BREWINGSTATION.get());
                out.accept(ObjectRegistry.DARK_BREW.get());
                out.accept(ObjectRegistry.BARREL_MAIN.get());
                out.accept(ObjectRegistry.BEER_MUG.get().asItem());
                out.accept(ObjectRegistry.BEER_WHEAT.get());
                out.accept(ObjectRegistry.BEER_BARLEY.get());
                out.accept(ObjectRegistry.BEER_HOPS.get());
                out.accept(ObjectRegistry.BEER_NETTLE.get());
                out.accept(ObjectRegistry.BEER_OAT.get());
                out.accept(ObjectRegistry.BEER_HALEY.get());
                out.accept(ObjectRegistry.WHISKEY_JOJANNIK.get());
                out.accept(ObjectRegistry.WHISKEY_LILITUSINGLEMALT.get());
                out.accept(ObjectRegistry.WHISKEY_CRISTELWALKER.get());
                out.accept(ObjectRegistry.WHISKEY_MAGGOALLAN.get());
                out.accept(ObjectRegistry.WHISKEY_CARRASCONLABEL.get());
                out.accept(ObjectRegistry.WHISKEY_AK.get());
                out.accept(ObjectRegistry.WHISKEY_HIGHLAND_HEARTH.get());
                out.accept(ObjectRegistry.WHISKEY_SMOKEY_REVERIE.get());
                out.accept(ObjectRegistry.WHISKEY_JAMESONS_MALT.get());
                out.accept(ObjectRegistry.PORK_KNUCKLE.get());
                out.accept(ObjectRegistry.FRIED_CHICKEN.get());
                out.accept(ObjectRegistry.DUMPLINGS.get());
                out.accept(ObjectRegistry.HALF_CHICKEN.get());
                out.accept(ObjectRegistry.SAUSAGE.get());
                out.accept(ObjectRegistry.MASHED_POTATOES.get());
                out.accept(ObjectRegistry.POTATO_SALAD.get());
                out.accept(ObjectRegistry.PRETZEL.get());
                out.accept(ObjectRegistry.GINGERBREAD.get());
                out.accept(ObjectRegistry.BREWFEST_HAT.get());
                out.accept(ObjectRegistry.BREWFEST_HAT_RED.get());
                out.accept(ObjectRegistry.BREWFEST_DRESS.get());
                out.accept(ObjectRegistry.BREWFEST_REGALIA.get());
                out.accept(ObjectRegistry.BREWFEST_TROUSERS.get());
                out.accept(ObjectRegistry.BREWFEST_BLOUSE.get());
                out.accept(ObjectRegistry.BREWFEST_BOOTS.get());
                out.accept(ObjectRegistry.BREWFEST_SHOES.get());
                out.accept(ObjectRegistry.BREATHALYZER.get());
                out.accept(ObjectRegistry.BREWERY_BANNER.get());
                out.accept(ObjectRegistry.BEER_ELEMENTAL_SPAWN_EGG.get());
            })
            .build());

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}
