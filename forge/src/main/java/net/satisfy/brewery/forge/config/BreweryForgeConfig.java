package net.satisfy.brewery.forge.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.io.File;

public class BreweryForgeConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue ENABLE_ZOMBIE_EQUIPMENT;

    public static final ForgeConfigSpec.IntValue SAUSAGE_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue SAUSAGE_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue PRETZEL_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue PRETZEL_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue PORK_KNUCKLE_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue PORK_KNUCKLE_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue FRIED_CHICKEN_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue FRIED_CHICKEN_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue HALF_CHICKEN_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue HALF_CHICKEN_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue MASHED_POTATOES_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue MASHED_POTATOES_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue POTATO_SALAD_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue POTATO_SALAD_SATURATION_MOD;
    public static final ForgeConfigSpec.IntValue DUMPLINGS_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue DUMPLINGS_SATURATION_MOD;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.push("Entities");

        ENABLE_ZOMBIE_EQUIPMENT = COMMON_BUILDER.define("enableZombieEquipment", true);

        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Items");
        COMMON_BUILDER.push("Nutrition");

        SAUSAGE_NUTRITION = COMMON_BUILDER.defineInRange("sausageNutrition", 6, 0, Integer.MAX_VALUE);
        SAUSAGE_SATURATION_MOD = COMMON_BUILDER.defineInRange("sausageSaturationMod", 0.5, 0.0, Double.MAX_VALUE);
        PRETZEL_NUTRITION = COMMON_BUILDER.defineInRange("pretzelNutrition", 3, 0, Integer.MAX_VALUE);
        PRETZEL_SATURATION_MOD = COMMON_BUILDER.defineInRange("pretzelSaturationMod", 0.4, 0.0, Double.MAX_VALUE);
        PORK_KNUCKLE_NUTRITION = COMMON_BUILDER.defineInRange("porkKnuckleNutrition", 6, 0, Integer.MAX_VALUE);
        PORK_KNUCKLE_SATURATION_MOD = COMMON_BUILDER.defineInRange("porkKnuckleSaturationMod", 0.6, 0.0, Double.MAX_VALUE);
        FRIED_CHICKEN_NUTRITION = COMMON_BUILDER.defineInRange("friedChickenNutrition", 6, 0, Integer.MAX_VALUE);
        FRIED_CHICKEN_SATURATION_MOD = COMMON_BUILDER.defineInRange("friedChickenSaturationMod", 0.6, 0.0, Double.MAX_VALUE);
        HALF_CHICKEN_NUTRITION = COMMON_BUILDER.defineInRange("halfChickenNutrition", 6, 0, Integer.MAX_VALUE);
        HALF_CHICKEN_SATURATION_MOD = COMMON_BUILDER.defineInRange("halfChickenSaturationMod", 0.6, 0.0, Double.MAX_VALUE);
        MASHED_POTATOES_NUTRITION = COMMON_BUILDER.defineInRange("mashedPotatoesNutrition", 3, 0, Integer.MAX_VALUE);
        MASHED_POTATOES_SATURATION_MOD = COMMON_BUILDER.defineInRange("mashedPotatoesSaturationMod", 0.5, 0.0, Double.MAX_VALUE);
        POTATO_SALAD_NUTRITION = COMMON_BUILDER.defineInRange("potatoSaladNutrition", 6, 0, Integer.MAX_VALUE);
        POTATO_SALAD_SATURATION_MOD = COMMON_BUILDER.defineInRange("potatoSaladSaturationMod", 0.7, 0.0, Double.MAX_VALUE);
        DUMPLINGS_NUTRITION = COMMON_BUILDER.defineInRange("dumplingsNutrition", 6, 0, Integer.MAX_VALUE);
        DUMPLINGS_SATURATION_MOD = COMMON_BUILDER.defineInRange("dumplingsSaturationMod", 0.5, 0.0, Double.MAX_VALUE);

        COMMON_BUILDER.pop();
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
    }

    public static void loadConfig(ForgeConfigSpec spec, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path))
                .sync()
                .preserveInsertionOrder()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        file.load();
        spec.setConfig(file);
        file.save();
    }
}