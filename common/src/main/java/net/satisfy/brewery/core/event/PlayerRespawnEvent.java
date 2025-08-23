package net.satisfy.brewery.core.event;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;

public class PlayerRespawnEvent implements PlayerEvent.PlayerRespawn {

    @Override
    public void respawn(ServerPlayer newPlayer, boolean conqueredEnd, Entity.RemovalReason removalReason) {
        if (newPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholManager.syncAlcohol(newPlayer, alcoholPlayer.brewery$getAlcohol());
        }
    }
}
