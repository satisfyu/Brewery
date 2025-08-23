package net.satisfy.brewery.forge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.client.BreweryClient;

@EventBusSubscriber(modid = Brewery.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class BreweryClientNeoForge {

    @SubscribeEvent
    public static void beforeClientSetup(RegisterEvent event) {
        BreweryClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BreweryClient.onInitializeClient();
    }
}
