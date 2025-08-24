package net.satisfy.brewery.core.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.core.network.handler.*;
import net.satisfy.brewery.core.network.packet.*;
import net.satisfy.brewery.core.util.BreweryIdentifier;

public class BreweryNetworking {
    public static final ResourceLocation DRINK_ALCOHOL_C2S_ID = BreweryIdentifier.identifier("drink_alcohol");
    public static final ResourceLocation DRUNK_EFFECT_S2C_ID = BreweryIdentifier.identifier("drink_alcohol");
    public static final ResourceLocation ALCOHOL_SYNC_S2C_ID = BreweryIdentifier.identifier("alcohol_sync");
    public static final ResourceLocation ALCOHOL_SYNC_REQUEST_C2S_ID = BreweryIdentifier.identifier("alcohol_sync_request");
    public static final ResourceLocation ATTACH_ROPE_S2C_ID = BreweryIdentifier.identifier("attach_rope");
    public static final ResourceLocation DETACH_ROPE_S2C_ID = BreweryIdentifier.identifier("detach_rope");
    public static final ResourceLocation SYNC_ROPE_S2C_ID = BreweryIdentifier.identifier("sync_rope");
    public static final ResourceLocation CHANGE_HANGING_ROPE_S2C_ID = BreweryIdentifier.identifier("change_hanging_rope");
    public static final ResourceLocation SET_SIGN_TEXT = BreweryIdentifier.identifier("set_sign_text");

    public static void registerC2SPackets() {
        //NetworkManager.registerReceiver(NetworkManager.Side.C2S, AlcoholSyncS2CPacket.TYPE, AlcoholSyncS2CPacket.STREAM_CODEC, new AlcoholSyncS2CPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DrinkAlcoholC2SPacket.TYPE, DrinkAlcoholC2SPacket.STREAM_CODEC, new DrinkAlcoholC2SPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SetWallDecorationTextPacket.TYPE, SetWallDecorationTextPacket.STREAM_CODEC, new SetWallDecorationTextPacketHandler());
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, AlcoholSyncS2CPacket.TYPE, AlcoholSyncS2CPacket.STREAM_CODEC, new AlcoholSyncS2CPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, AttachRopeS2CPacket.TYPE, AttachRopeS2CPacket.STREAM_CODEC, new AttachRopeS2CPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DrunkEffectS2CPacket.TYPE, DrunkEffectS2CPacket.STREAM_CODEC, new DrunkEffectS2CPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DetachRopeS2CPacket.TYPE, DetachRopeS2CPacket.STREAM_CODEC, new DetachRopeS2CPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncRequestC2SPacket.TYPE, SyncRequestC2SPacket.STREAM_CODEC, new SyncRequestC2SPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ChangeHangingRopeS2CPacket.TYPE, ChangeHangingRopeS2CPacket.STREAM_CODEC, new ChangeHangingRopeS2CPacketHandler());
    }

    public static void sendSetSignTextToServer(SetWallDecorationTextPacket packet) {
        NetworkManager.sendToServer(packet);
    }
}
