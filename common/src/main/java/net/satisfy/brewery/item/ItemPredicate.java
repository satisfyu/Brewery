package net.satisfy.brewery.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.satisfy.brewery.registry.*;
import net.satisfy.brewery.util.BreweryIdentifier;

public class ItemPredicate {
    public static void register() {
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), new BreweryIdentifier("breathing"), (itemStack, clientLevel, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), new BreweryIdentifier("drunkenness"), (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.hasTag()) {
                String drunkenness = itemStack.getTag().getString("brewery.drunkenness");
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
