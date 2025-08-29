package net.satisfy.brewery.core.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.registry.ObjectRegistry;

public class ItemPredicate {
    public static void register() {
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), Brewery.identifier("breathing"), (itemStack, clientLevel, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), Brewery.identifier("drunkenness"), (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.has(DataComponents.CUSTOM_DATA)) {
                String drunkenness = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("brewery.drunkenness");
                return switch (drunkenness) {
                    case "DANGER" -> 0.9F;
                    case "WARNING" -> 0.6F;
                    case "EASY" -> 0.3F;
                    default -> 0.0F;
                };
            }
            return 0.0F;
        });
    }
}
