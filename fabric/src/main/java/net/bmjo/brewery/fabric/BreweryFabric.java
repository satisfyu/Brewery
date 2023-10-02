package net.bmjo.brewery.fabric;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.fabric.registry.BreweryFabricVillagers;
import net.bmjo.brewery.fabric.world.BreweryBiomeModification;
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
