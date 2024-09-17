package net.satisfy.brewery.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.satisfy.brewery.registry.ObjectRegistry;
import net.satisfy.brewery.util.BreweryIdentifier;

public class ItemPredicate {
    public static void register() {
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), BreweryIdentifier.of("breathing"), (itemStack, clientLevel, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), BreweryIdentifier.of("drunkenness"), (itemStack, clientLevel, livingEntity, i) -> {
            CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            String drunkenness = customData.copyTag().getString("brewery.drunkenness");
            return switch (drunkenness) {
                case "DANGER" -> 0.9F;
                case "WARNING" -> 0.6F;
                case "EASY" -> 0.3F;
                default -> 0.0F;
            };
        });
    }
}
