package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.network.packet.SyncRequestC2SPacket;

public class SyncRequestC2SPacketHandler implements NetworkManager.NetworkReceiver<SyncRequestC2SPacket>{

    @Override
    public void receive(SyncRequestC2SPacket packet, NetworkManager.PacketContext context) {
        ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();
        if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.brewery$getAlcohol());
        }
    }
}
