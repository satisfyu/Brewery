package net.satisfy.brewery.util;

import net.satisfy.brewery.Brewery;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface BreweryBlockItem
{
    default boolean hasItem()
    {
        return true;
    }

    default Item.Properties makeItemSettings(CreativeModeTab group)
    {
        return new Item.Properties().tab(group);
    }

    default Item makeItem(CreativeModeTab group)
    {
        return new BlockItem((Block) this, makeItemSettings(group));
    }

    default Item makeItem()
    {
        return new BlockItem((Block) this, makeItemSettings(Brewery.CREATIVE_TAB));
    }

    default void registerItem(ResourceLocation id, CreativeModeTab group)
    {
        BreweryUtil.registerObject(Registry.ITEM, id, makeItem(group));
    }
}