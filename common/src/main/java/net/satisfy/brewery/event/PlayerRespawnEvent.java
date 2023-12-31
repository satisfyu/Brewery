package net.satisfy.brewery.event;

import dev.architectury.event.events.common.PlayerEvent;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerRespawnEvent implements PlayerEvent.PlayerRespawn {

    @Override
    public void respawn(ServerPlayer newPlayer, boolean conqueredEnd) {
        if (newPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholManager.syncAlcohol(newPlayer, alcoholPlayer.getAlcohol());
        }
    }
}
