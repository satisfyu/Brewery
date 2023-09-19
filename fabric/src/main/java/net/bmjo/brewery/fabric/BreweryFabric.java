package net.bmjo.brewery.fabric;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.fabric.registry.BreweryFabricVillagers;
import net.fabricmc.api.ModInitializer;

public class BreweryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Brewery.init();
        BreweryFabricVillagers.init();
    }
}
