package net.satisfy.brewery.core.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.network.handler.AlcoholSyncS2CPacketHandler;
import net.satisfy.brewery.core.network.handler.DrinkAlcoholC2SPacketHandler;
import net.satisfy.brewery.core.network.handler.DrunkEffectS2CPacketHandler;
import net.satisfy.brewery.core.network.handler.SetWallDecorationTextPacketHandler;
import net.satisfy.brewery.core.network.handler.SyncRequestC2SPacketHandler;
import net.satisfy.brewery.core.network.packet.AlcoholSyncS2CPacket;
import net.satisfy.brewery.core.network.packet.DrinkAlcoholC2SPacket;
import net.satisfy.brewery.core.network.packet.DrunkEffectS2CPacket;
import net.satisfy.brewery.core.network.packet.SetWallDecorationTextPacket;
import net.satisfy.brewery.core.network.packet.SyncRequestC2SPacket;

public class BreweryNetworking {
    public static final ResourceLocation DRINK_ALCOHOL_C2S_ID = Brewery.identifier("drink_alcohol");
    public static final ResourceLocation DRUNK_EFFECT_S2C_ID = Brewery.identifier("drunk_effect");
    public static final ResourceLocation ALCOHOL_SYNC_S2C_ID = Brewery.identifier("alcohol_sync");
    public static final ResourceLocation ALCOHOL_SYNC_REQUEST_C2S_ID = Brewery.identifier("alcohol_sync_request");
    public static final ResourceLocation SET_SIGN_TEXT = Brewery.identifier("set_sign_text");

    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DrinkAlcoholC2SPacket.TYPE, DrinkAlcoholC2SPacket.STREAM_CODEC, new DrinkAlcoholC2SPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SyncRequestC2SPacket.TYPE, SyncRequestC2SPacket.STREAM_CODEC, new SyncRequestC2SPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SetWallDecorationTextPacket.TYPE, SetWallDecorationTextPacket.STREAM_CODEC, new SetWallDecorationTextPacketHandler());
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, AlcoholSyncS2CPacket.TYPE, AlcoholSyncS2CPacket.STREAM_CODEC, new AlcoholSyncS2CPacketHandler());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DrunkEffectS2CPacket.TYPE, DrunkEffectS2CPacket.STREAM_CODEC, new DrunkEffectS2CPacketHandler());
    }

    public static void sendSetSignTextToServer(SetWallDecorationTextPacket packet) {
        NetworkManager.sendToServer(packet);
    }
}
