package net.satisfy.brewery.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class PlatformHelper {
    @ExpectPlatform
    public static boolean isZombieEquipmentEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getNutrition(String itemName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getSaturationMod(String itemName) {
        throw new AssertionError();
    }
}