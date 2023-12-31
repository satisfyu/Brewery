package net.satisfy.brewery.forge.client;

import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.client.BreweryClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = Brewery.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreweryClientForge {

    @SubscribeEvent
    public static void beforeClientSetup(RegisterEvent event) {
        BreweryClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BreweryClient.onInitializeClient();
    }

}
