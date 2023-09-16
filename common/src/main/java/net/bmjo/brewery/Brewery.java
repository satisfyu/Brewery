package net.bmjo.brewery;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.CreativeTabRegistry;
import net.bmjo.brewery.event.PlayerCloneEvent;
import net.bmjo.brewery.event.PlayerJoinEvent;
import net.bmjo.brewery.event.PlayerRespawnEvent;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.*;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.registry.EffectRegistry;
import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.sound.SoundRegistry;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Brewery {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final CreativeModeTab CREATIVE_TAB = CreativeTabRegistry.create(new BreweryIdentifier("creative_tab"), () -> new ItemStack(ObjectRegistry.BEER_KEG.get()));

    public static void init() {
        LOGGER.debug("Initiate " + MOD_ID);
        ObjectRegistry.register();
        EffectRegistry.registerEffects();
        BreweryNetworking.registerC2SPackets();
        SoundRegistry.registerSounds();
        CompostablesRegistry.init();
        EntityRegistry.register();
        BlockEntityRegistry.registerBlockEntities();
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(new PlayerJoinEvent());
        PlayerEvent.PLAYER_RESPAWN.register(new PlayerRespawnEvent());
        PlayerEvent.PLAYER_CLONE.register(new PlayerCloneEvent());
    }

    public static ResourceLocation MOD_ID(String path)
    {
        return new ResourceLocation(Brewery.MOD_ID, path);
    }
}
