package net.satisfy.brewery.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.brewery.Brewery;

import java.util.List;
import java.util.Set;

public class StorageTypeRegistry {
    public static final ResourceLocation BEVERAGE = Brewery.identifier("beverage");

    public static Set<Block> registerBlocks(Set<Block> blocks) {
        blocks.add(ObjectRegistry.BEER_MUG.get());
        blocks.addAll(List.of(
                ObjectRegistry.BEER_BARLEY.get(), ObjectRegistry.BEER_HALEY.get(), ObjectRegistry.BEER_HOPS.get(), ObjectRegistry.BEER_NETTLE.get(),
                ObjectRegistry.WHISKEY_CARRASCONLABEL.get(), ObjectRegistry.WHISKEY_CRISTELWALKER.get(),
                ObjectRegistry.WHISKEY_JOJANNIK.get(), ObjectRegistry.WHISKEY_MAGGOALLAN.get(), ObjectRegistry.BEER_WHEAT.get(),
                ObjectRegistry.WHISKEY_LILITUSINGLEMALT.get(), ObjectRegistry.BEER_OAT.get(), ObjectRegistry.WHISKEY_AK.get(),
                ObjectRegistry.WHISKEY_HIGHLAND_HEARTH.get(), ObjectRegistry.WHISKEY_JAMESONS_MALT.get(),
                ObjectRegistry.WHISKEY_SMOKEY_REVERIE.get()
        ));
        return blocks;
    }
}
