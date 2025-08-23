package net.satisfy.brewery.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.registry.CompostablesRegistry;

@Mod(Brewery.MOD_ID)
public class BreweryNeoForge {
    public BreweryNeoForge(final IEventBus modEventBus, final ModContainer modContainer) {
        EventBusesHooks.whenAvailable(Brewery.MOD_ID, IEventBus::start);
        Brewery.init();
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostablesRegistry::init);
    }
}

