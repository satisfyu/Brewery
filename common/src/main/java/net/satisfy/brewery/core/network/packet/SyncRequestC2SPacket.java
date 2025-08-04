package net.satisfy.brewery.core.network.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;

public class SyncRequestC2SPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();
        if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.brewery$getAlcohol());
        }
    }
}
