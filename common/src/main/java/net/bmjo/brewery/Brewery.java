package net.bmjo.brewery;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.bmjo.brewery.effect.BreweryEffects;
import net.bmjo.brewery.event.PlayerCloneEvent;
import net.bmjo.brewery.event.PlayerJoinEvent;
import net.bmjo.brewery.event.PlayerRespawnEvent;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.BlockEntityRegister;
import net.bmjo.brewery.registry.EntityRegister;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.bmjo.brewery.sound.SoundRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Brewery {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        LOGGER.debug("Initiate " + MOD_ID);
        ObjectRegistry.register();
        BreweryEffects.registerEffects();
        BreweryNetworking.registerC2SPackets();
        SoundRegistry.registerSounds();
        EntityRegister.register();
        BlockEntityRegister.registerBlockEntities();
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(new PlayerJoinEvent());
        PlayerEvent.PLAYER_RESPAWN.register(new PlayerRespawnEvent());
        PlayerEvent.PLAYER_CLONE.register(new PlayerCloneEvent());
    }
}
