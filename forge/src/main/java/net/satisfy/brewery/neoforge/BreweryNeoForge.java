package net.satisfy.brewery.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.neoforge.client.BreweryClientForge;
import net.satisfy.brewery.neoforge.registry.BreweryForgeVillagers;
import net.satisfy.brewery.registry.CompostablesRegistry;

@Mod(Brewery.MOD_ID)
public class BreweryNeoForge {
    public BreweryNeoForge(IEventBus modEventBus) {
        Brewery.init();
        modEventBus.addListener(this::commonSetup);
        BreweryForgeVillagers.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostablesRegistry::init);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BreweryClientForge.entityRendererSetup();
    }
}
