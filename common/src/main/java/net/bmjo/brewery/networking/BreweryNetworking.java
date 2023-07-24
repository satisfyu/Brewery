package net.bmjo.brewery.networking;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.bmjo.brewery.networking.packet.AlcoholSyncS2CPacket;
import net.bmjo.brewery.networking.packet.DrinkAlcoholC2SPacket;
import net.bmjo.brewery.networking.packet.DrunkEffectS2CPacket;
import net.bmjo.brewery.networking.packet.SyncRequestC2SPacket;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class BreweryNetworking {
    public static final ResourceLocation DRINK_ALCOHOL_C2S_ID = new BreweryIdentifier("drink_alcohol");
    public static final ResourceLocation DRUNK_EFFECT_S2C_ID = new BreweryIdentifier("drink_alcohol");
    public static final ResourceLocation ALCOHOL_SYNC_S2C_ID = new BreweryIdentifier("alcohol_sync");
    public static final ResourceLocation ALCOHOL_SYNC_REQUEST_C2S_ID = new BreweryIdentifier("alcohol_sync_request");

    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ALCOHOL_SYNC_REQUEST_C2S_ID, new SyncRequestC2SPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DRINK_ALCOHOL_C2S_ID, new DrinkAlcoholC2SPacket());
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ALCOHOL_SYNC_S2C_ID, new AlcoholSyncS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DRUNK_EFFECT_S2C_ID, new DrunkEffectS2CPacket());
    }

    public static FriendlyByteBuf createPacketBuf(){
        return new FriendlyByteBuf(Unpooled.buffer());
    }

}
