package net.bmjo.brewery.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface RopeEntity {
    static boolean canDestroyWith(ItemStack item) {
        return item.is(Items.SHEARS);
    }

    void destroyConnections(boolean mayDrop);
}
