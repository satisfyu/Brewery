package net.bmjo.brewery.event;

import dev.architectury.event.events.common.PlayerEvent;
import net.bmjo.brewery.alcohol.AlcoholManager;
import net.bmjo.brewery.alcohol.AlcoholPlayer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerRespawnEvent implements PlayerEvent.PlayerRespawn {

    @Override
    public void respawn(ServerPlayer newPlayer, boolean conqueredEnd) {
        if (newPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholManager.syncAlcohol(newPlayer, alcoholPlayer.getAlcohol());
        }
    }
}
