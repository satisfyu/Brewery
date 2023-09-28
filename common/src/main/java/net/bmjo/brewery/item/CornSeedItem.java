package net.bmjo.brewery.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.bmjo.brewery.registry.*;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

public class CornSeedItem extends BlockItem {
    public CornSeedItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static void handleStoneCutter(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity item) {
            ItemStack stack = item.getItem();
            if (!stack.is(Items.WHEAT_SEEDS)) return;

            Supplier<Item> supplier = ObjectRegistry.SEEDCONVERSION.entrySet()
                    .stream()
                    .filter(entry -> stack.is(entry.getKey().get()))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(null);

            if (supplier == null) return;
            ItemStack newStack = supplier.get().getDefaultInstance();

            newStack.setCount(stack.getCount() * 2);
            if (!newStack.isEmpty()) {
                entity.discard();
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, newStack);
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().scale(0.5));
                level.addFreshEntity(itemEntity);
            }
        }
    }
}