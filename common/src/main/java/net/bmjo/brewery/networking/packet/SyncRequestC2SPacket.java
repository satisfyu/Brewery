package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.effect.alcohol.AlcoholManager;
import net.bmjo.brewery.effect.alcohol.AlcoholPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SyncRequestC2SPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
    ServerPlayer serverPlayer = (ServerPlayer)context.getPlayer();
        if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.getAlcohol());
        }
    }
}
