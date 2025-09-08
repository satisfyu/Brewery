package net.satisfy.brewery.core.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.network.packet.*;


public class BreweryNetworking {
    public static final ResourceLocation SET_SIGN_TEXT = Brewery.identifier("set_sign_text");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SetWallDecorationTextPacket.TYPE, SetWallDecorationTextPacket.STREAM_CODEC, (pkt, ctx) -> ctx.queue(() -> SetWallDecorationTextPacket.handle(pkt, (ServerPlayer) ctx.getPlayer())));
    }

    public static void sendSetSignTextToServer(SetWallDecorationTextPacket packet) {
        NetworkManager.sendToServer(packet);
    }
}
