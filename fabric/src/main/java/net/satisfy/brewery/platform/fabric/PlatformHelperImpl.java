package net.satisfy.brewery.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.satisfy.brewery.fabric.core.config.BreweryFabricConfig;

public class PlatformHelperImpl {
    public static boolean isZombieEquipmentEnabled() {
        BreweryFabricConfig config = AutoConfig.getConfigHolder(BreweryFabricConfig.class).getConfig();
        return config.entities.enableZombieEquipment;
    }

    public static int getNutrition(String itemName) {
        BreweryFabricConfig.ItemsSettings items = AutoConfig.getConfigHolder(BreweryFabricConfig.class).getConfig().items;
        return switch (itemName) {
            case "sausage" -> items.nutrition.sausageNutrition;
            case "pretzel" -> items.nutrition.pretzelNutrition;
            case "pork_knuckle" -> items.nutrition.porkKnuckleNutrition;
            case "fried_chicken" -> items.nutrition.friedChickenNutrition;
            case "half_chicken" -> items.nutrition.halfChickenNutrition;
            case "mashed_potatoes" -> items.nutrition.mashedPotatoesNutrition;
            case "potato_salad" -> items.nutrition.potatoSaladNutrition;
            case "dumplings" -> items.nutrition.dumplingsNutrition;
            default -> 0;
        };
    }

    public static float getSaturationMod(String itemName) {
        BreweryFabricConfig.ItemsSettings items = AutoConfig.getConfigHolder(BreweryFabricConfig.class).getConfig().items;
        return switch (itemName) {
            case "sausage" -> items.nutrition.sausageSaturationMod;
            case "pretzel" -> items.nutrition.pretzelSaturationMod;
            case "pork_knuckle" -> items.nutrition.porkKnuckleSaturationMod;
            case "fried_chicken" -> items.nutrition.friedChickenSaturationMod;
            case "half_chicken" -> items.nutrition.halfChickenSaturationMod;
            case "mashed_potatoes" -> items.nutrition.mashedPotatoesSaturationMod;
            case "potato_salad" -> items.nutrition.potatoSaladSaturationMod;
            case "dumplings" -> items.nutrition.dumplingsSaturationMod;
            default -> 0;
        };
    }

}
