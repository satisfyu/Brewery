package net.satisfy.brewery.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.fabric.core.config.BreweryFabricConfig;
import net.satisfy.brewery.fabric.core.world.BreweryBiomeModification;

public class BreweryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(BreweryFabricConfig.class, GsonConfigSerializer::new);

        Brewery.init();
        BreweryBiomeModification.init();
    }
}
