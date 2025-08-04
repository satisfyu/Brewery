package net.satisfy.brewery;

import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.satisfy.brewery.core.event.brew_event.BrewEvents;
import net.satisfy.brewery.core.event.*;
import net.satisfy.brewery.core.network.BreweryNetworking;
import net.satisfy.brewery.core.registry.*;

public class Brewery {
    public static final String MOD_ID = "brewery";
    public static void init() {
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        MobEffectRegistry.init();
        SoundEventRegistry.init();
        BrewEvents.loadClass();
        CommonEvents.init();
        BreweryNetworking.registerC2SPackets();
        RecipeTypeRegistry.init();
        TabRegistry.init();
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
