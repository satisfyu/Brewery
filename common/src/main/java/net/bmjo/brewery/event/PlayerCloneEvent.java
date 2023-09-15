package net.bmjo.brewery.event;

import dev.architectury.event.events.common.PlayerEvent;
import net.bmjo.brewery.effect.alcohol.AlcoholLevel;
import net.bmjo.brewery.effect.alcohol.AlcoholPlayer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCloneEvent implements PlayerEvent.PlayerClone {
    @Override
    public void clone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) {
        if (newPlayer instanceof AlcoholPlayer newAlcoholPlayer && oldPlayer instanceof AlcoholPlayer oldAlcoholPlayer) {
            AlcoholLevel alcoholLevel = oldAlcoholPlayer.getAlcohol().copy();
            alcoholLevel.soberUp();
            newAlcoholPlayer.setAlcohol(alcoholLevel);
        }
    }
}
