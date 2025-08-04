package net.satisfy.brewery.core.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.brewery.core.network.packet.*;
import net.satisfy.brewery.core.util.BreweryIdentifier;

public class BreweryNetworking {
    public static final ResourceLocation DRINK_ALCOHOL_C2S_ID = new BreweryIdentifier("drink_alcohol");
    public static final ResourceLocation DRUNK_EFFECT_S2C_ID = new BreweryIdentifier("drink_alcohol");
    public static final ResourceLocation ALCOHOL_SYNC_S2C_ID = new BreweryIdentifier("alcohol_sync");
    public static final ResourceLocation ALCOHOL_SYNC_REQUEST_C2S_ID = new BreweryIdentifier("alcohol_sync_request");
    public static final ResourceLocation ATTACH_ROPE_S2C_ID = new BreweryIdentifier("attach_rope");
    public static final ResourceLocation DETACH_ROPE_S2C_ID = new BreweryIdentifier("detach_rope");
    public static final ResourceLocation SYNC_ROPE_S2C_ID = new BreweryIdentifier("sync_rope");
    public static final ResourceLocation CHANGE_HANGING_ROPE_S2C_ID = new BreweryIdentifier("change_hanging_rope");
    public static final ResourceLocation SET_SIGN_TEXT = new BreweryIdentifier("set_sign_text");

    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ALCOHOL_SYNC_REQUEST_C2S_ID, new SyncRequestC2SPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DRINK_ALCOHOL_C2S_ID, new DrinkAlcoholC2SPacket());

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SET_SIGN_TEXT, (buf, context) -> {
            SetWallDecorationTextPacket packet = SetWallDecorationTextPacket.decode(buf);
            context.queue(() -> SetWallDecorationTextPacket.handle(packet, (ServerPlayer) context.getPlayer()));
        });
    }
    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ALCOHOL_SYNC_S2C_ID, new AlcoholSyncS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DRUNK_EFFECT_S2C_ID, new DrunkEffectS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ATTACH_ROPE_S2C_ID, new AttachRopeS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DETACH_ROPE_S2C_ID, new DetachRopeS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_ROPE_S2C_ID, new SyncRopeS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANGE_HANGING_ROPE_S2C_ID, new ChangeHangingRopeS2CPacket());
    }


    public static FriendlyByteBuf createPacketBuf() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static void sendSetSignTextToServer(SetWallDecorationTextPacket packet) {
        FriendlyByteBuf buf = createPacketBuf();
        SetWallDecorationTextPacket.encode(packet, buf);
        NetworkManager.sendToServer(SET_SIGN_TEXT, buf);
    }
}
