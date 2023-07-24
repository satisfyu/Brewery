package net.bmjo.brewery.forge;

import dev.architectury.platform.forge.EventBuses;
import net.bmjo.brewery.Brewery;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Brewery.MOD_ID)
public class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let architectury registerSounds our content on the right time
        EventBuses.registerModEventBus(Brewery.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Brewery.init();
    }
}
