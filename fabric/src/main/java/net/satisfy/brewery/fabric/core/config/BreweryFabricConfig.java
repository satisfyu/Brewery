package net.satisfy.brewery.fabric.core.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "brewery")
@Config.Gui.Background("farm_and_charm:textures/items/brewfest_hat.png")
public class BreweryFabricConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public EntitySettings entities = new EntitySettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ItemsSettings items = new ItemsSettings();

    public static class EntitySettings {
        public boolean enableZombieEquipment = true;
    }

    public static class ItemsSettings {
        @ConfigEntry.Gui.CollapsibleObject
        public NutritionSettings nutrition = new NutritionSettings();

        public static class NutritionSettings {
            public int sausageNutrition = 6;
            public float sausageSaturationMod = 0.5f;
            public int pretzelNutrition = 3;
            public float pretzelSaturationMod = 0.4f;
            public int porkKnuckleNutrition = 6;
            public float porkKnuckleSaturationMod = 0.6f;
            public int friedChickenNutrition = 6;
            public float friedChickenSaturationMod = 0.6f;
            public int halfChickenNutrition = 6;
            public float halfChickenSaturationMod = 0.6f;
            public int mashedPotatoesNutrition = 3;
            public float mashedPotatoesSaturationMod = 0.5f;
            public int potatoSaladNutrition = 6;
            public float potatoSaladSaturationMod = 0.7f;
            public int dumplingsNutrition = 6;
            public float dumplingsSaturationMod = 0.5f;
        }
    }
}
