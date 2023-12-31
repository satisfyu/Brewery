package net.satisfy.brewery.fabric.client;

import net.satisfy.brewery.client.BreweryClient;
import net.fabricmc.api.ClientModInitializer;

public class BreweryClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BreweryClient.onInitializeClient();
        BreweryClient.preInitClient();
    }
}
