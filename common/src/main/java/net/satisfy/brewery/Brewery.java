package net.satisfy.brewery;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.core.event.CommonEvents;
import net.satisfy.brewery.core.event.PartyStarterEvent;
import net.satisfy.brewery.core.event.PlayerCloneEvent;
import net.satisfy.brewery.core.event.PlayerRespawnEvent;
import net.satisfy.brewery.core.event.brew_event.BrewEvents;
import net.satisfy.brewery.core.network.BreweryNetworking;
import net.satisfy.brewery.core.registry.*;

public class Brewery {
    public static final String MOD_ID = "brewery";

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

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
    }
}
