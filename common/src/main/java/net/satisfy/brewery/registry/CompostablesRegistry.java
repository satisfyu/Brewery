package net.satisfy.brewery.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

public class CompostablesRegistry {
    
    public static void init(){
        registerCompostableItem(ObjectRegistry.CORN, 0.4F);
        registerCompostableItem(ObjectRegistry.CORN_SEEDS, 0.2F);
        registerCompostableItem(ObjectRegistry.DRIED_CORN, 0.6F);
        registerCompostableItem(ObjectRegistry.BARLEY, 0.4F);
        registerCompostableItem(ObjectRegistry.BARLEY_SEEDS, 0.2F);
        registerCompostableItem(ObjectRegistry.DRIED_BARLEY, 0.6F);
        registerCompostableItem(ObjectRegistry.HOPS, 0.4F);
        registerCompostableItem(ObjectRegistry.HOPS_SEEDS, 0.2F);
        registerCompostableItem(ObjectRegistry.DRIED_WHEAT, 0.6F);
    }

    public static <T extends ItemLike> void registerCompostableItem(RegistrySupplier<T> item, float chance) {
        if (item.get().asItem() != Items.AIR) {
            ComposterBlock.COMPOSTABLES.put(item.get(), chance);
        }
    }
}
