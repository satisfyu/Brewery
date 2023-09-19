package net.bmjo.brewery.forge;

import dev.architectury.platform.forge.EventBuses;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.forge.registry.BreweryForgeVillagers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Brewery.MOD_ID)
public class BreweryForge {
    public BreweryForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Brewery.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Brewery.init();
        BreweryForgeVillagers.register(modEventBus);
    }
}
