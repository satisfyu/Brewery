package net.satisfy.brewery.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.satisfy.brewery.client.BreweryClient;
import net.satisfy.brewery.core.item.BreathalyzerItem;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.fabric.client.renderer.BrewfestBootsRenderer;
import net.satisfy.brewery.fabric.client.renderer.BrewfestChestplateRenderer;
import net.satisfy.brewery.fabric.client.renderer.BrewfestHatRenderer;
import net.satisfy.brewery.fabric.client.renderer.BrewfestLeggingsRenderer;

public class BreweryClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BreweryClient.preInitClient();
        BreathalyzerItem.init();
        BreweryClient.onInitializeClient();

        ArmorRenderer.register(new BrewfestBootsRenderer(), ObjectRegistry.BREWFEST_BOOTS.get(), ObjectRegistry.BREWFEST_SHOES.get());
        ArmorRenderer.register(new BrewfestHatRenderer(), ObjectRegistry.BREWFEST_HAT.get(), ObjectRegistry.BREWFEST_HAT_RED.get());
        ArmorRenderer.register(new BrewfestChestplateRenderer(), ObjectRegistry.BREWFEST_REGALIA.get(), ObjectRegistry.BREWFEST_BLOUSE.get());
        ArmorRenderer.register(new BrewfestLeggingsRenderer(), ObjectRegistry.BREWFEST_TROUSERS.get(), ObjectRegistry.BREWFEST_DRESS.get());
    }
}