package net.bmjo.brewery.forge;

import dev.architectury.platform.forge.EventBuses;
import net.bmjo.brewery.Brewery;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Brewery.MOD_ID)
public class BreweryForge {
    public BreweryForge() {
        EventBuses.registerModEventBus(Brewery.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Brewery.init();
    }
}
