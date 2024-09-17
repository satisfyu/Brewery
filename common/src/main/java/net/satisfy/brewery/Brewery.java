package net.satisfy.brewery;

import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.satisfy.brewery.block.brew_event.BrewEvents;
import net.satisfy.brewery.event.*;
import net.satisfy.brewery.networking.BreweryNetworking;
import net.satisfy.brewery.registry.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Brewery {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        LOGGER.debug("Initiate " + MOD_ID);
        DataFixerRegistry.init();
        ObjectRegistry.init();
        TabRegistry.init();
        ArmorMaterialRegistry.init();
        BlockEntityRegistry.init();
        EntityRegistry.init();
        MobEffectRegistry.init();
        BrewEvents.loadClass();
        CommonEvents.init();
        BreweryNetworking.registerC2SPackets();
        RecipeTypeRegistry.init();
        registerEvents();

    }

    private static void registerEvents() {
        PartyStarterEvent partyStarterEvent = new PartyStarterEvent();
        PlayerEvent.PLAYER_RESPAWN.register(new PlayerRespawnEvent());
        PlayerEvent.ATTACK_ENTITY.register(partyStarterEvent);
        PlayerEvent.PLAYER_CLONE.register(new PlayerCloneEvent());
        InteractionEvent.RIGHT_CLICK_BLOCK.register(new BlockClickEvent());
    }
}
