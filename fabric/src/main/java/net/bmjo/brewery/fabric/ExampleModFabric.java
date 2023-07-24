package net.bmjo.brewery.fabric;

import net.bmjo.brewery.Brewery;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Brewery.init();
    }
}
