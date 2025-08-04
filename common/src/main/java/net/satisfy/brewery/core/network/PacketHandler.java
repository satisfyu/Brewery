package net.satisfy.brewery.core.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketHandler {
    public static final ResourceLocation SET_SIGN_TEXT = new ResourceLocation("brewery", "set_sign_text");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SET_SIGN_TEXT, (buf, context) -> {
            SetWallDecorationTextPacket packet = SetWallDecorationTextPacket.decode(buf);
            context.queue(() -> SetWallDecorationTextPacket.handle(packet, (ServerPlayer) context.getPlayer()));
        });
    }

    public static void sendToServer(SetWallDecorationTextPacket packet) {
        
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        SetWallDecorationTextPacket.encode(packet, buf);
        NetworkManager.sendToServer(SET_SIGN_TEXT, buf);
    }
}
