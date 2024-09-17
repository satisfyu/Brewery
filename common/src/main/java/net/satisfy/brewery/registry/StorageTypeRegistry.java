package net.satisfy.brewery.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.brewery.util.BreweryIdentifier;

import java.util.List;
import java.util.Set;

import static net.satisfy.brewery.registry.ObjectRegistry.*;

public class StorageTypeRegistry {
    public static final ResourceLocation BEVERAGE = BreweryIdentifier.of("beverage");

    public static void registerBlocks(Set<Block> blocks) {
        blocks.addAll(List.of(
                BEER_BARLEY.get(), BEER_HALEY.get(), BEER_HOPS.get(), BEER_NETTLE.get(),
                WHISKEY_CARRASCONLABEL.get(), WHISKEY_CRISTELWALKER.get(),
                WHISKEY_JOJANNIK.get(), WHISKEY_MAGGOALLAN.get(), BEER_WHEAT.get(),
                WHISKEY_LILITUSINGLEMALT.get(), BEER_OAT.get(), WHISKEY_AK.get(),
                WHISKEY_HIGHLAND_HEARTH.get(), WHISKEY_JAMESONS_MALT.get(),
                WHISKEY_SMOKEY_REVERIE.get()
        ));
    }
}
