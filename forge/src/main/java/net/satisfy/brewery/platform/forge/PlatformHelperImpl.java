package net.satisfy.brewery.platform.forge;

import net.satisfy.brewery.forge.config.BreweryForgeConfig;

public abstract class PlatformHelperImpl {
    public static boolean isZombieEquipmentEnabled() {
        return BreweryForgeConfig.ENABLE_ZOMBIE_EQUIPMENT.get();
    }

    public static int getNutrition(String itemName) {
        return switch (itemName) {
            case "sausage" -> BreweryForgeConfig.SAUSAGE_NUTRITION.get();
            case "pretzel" -> BreweryForgeConfig.PRETZEL_NUTRITION.get();
            case "pork_knuckle" -> BreweryForgeConfig.PORK_KNUCKLE_NUTRITION.get();
            case "fried_chicken" -> BreweryForgeConfig.FRIED_CHICKEN_NUTRITION.get();
            case "half_chicken" -> BreweryForgeConfig.HALF_CHICKEN_NUTRITION.get();
            case "mashed_potatoes" -> BreweryForgeConfig.MASHED_POTATOES_NUTRITION.get();
            case "potato_salad" -> BreweryForgeConfig.POTATO_SALAD_NUTRITION.get();
            case "dumplings" -> BreweryForgeConfig.DUMPLINGS_NUTRITION.get();
            default -> 0;
        };
    }

    public static float getSaturationMod(String itemName) {
        return switch (itemName) {
            case "sausage" -> BreweryForgeConfig.SAUSAGE_SATURATION_MOD.get().floatValue();
            case "pretzel" -> BreweryForgeConfig.PRETZEL_SATURATION_MOD.get().floatValue();
            case "pork_knuckle" -> BreweryForgeConfig.PORK_KNUCKLE_SATURATION_MOD.get().floatValue();
            case "fried_chicken" -> BreweryForgeConfig.FRIED_CHICKEN_SATURATION_MOD.get().floatValue();
            case "half_chicken" -> BreweryForgeConfig.HALF_CHICKEN_SATURATION_MOD.get().floatValue();
            case "mashed_potatoes" -> BreweryForgeConfig.MASHED_POTATOES_SATURATION_MOD.get().floatValue();
            case "potato_salad" -> BreweryForgeConfig.POTATO_SALAD_SATURATION_MOD.get().floatValue();
            case "dumplings" -> BreweryForgeConfig.DUMPLINGS_SATURATION_MOD.get().floatValue();
            default -> 0;
        };
    }
}