package net.satisfy.brewery.fabric;

import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.fabric.registry.BreweryFabricVillagers;
import net.satisfy.brewery.fabric.world.BreweryBiomeModification;
import net.fabricmc.api.ModInitializer;

public class BreweryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Brewery.init();
        Brewery.commonSetup();
        BreweryFabricVillagers.init();
        BreweryBiomeModification.init();
    }
}
