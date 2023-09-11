package net.bmjo.brewery.networking;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.bmjo.brewery.networking.packet.*;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class BreweryNetworking { //TODO add incomplete connections
    public static final ResourceLocation DRINK_ALCOHOL_C2S_ID = new BreweryIdentifier("drink_alcohol");
    public static final ResourceLocation DRUNK_EFFECT_S2C_ID = new BreweryIdentifier("drink_alcohol");
    public static final ResourceLocation ALCOHOL_SYNC_S2C_ID = new BreweryIdentifier("alcohol_sync");
    public static final ResourceLocation ALCOHOL_SYNC_REQUEST_C2S_ID = new BreweryIdentifier("alcohol_sync_request");
    public static final ResourceLocation DEACTIVATE_KETTLE_S2C_ID = new BreweryIdentifier("deactivate_kettle");
    public static final ResourceLocation ATTACH_ROPE_S2C_ID = new BreweryIdentifier("attach_rope");
    public static final ResourceLocation DETACH_ROPE_S2C_ID = new BreweryIdentifier("detach_rope");
    public static final ResourceLocation SYNC_ROPE_S2C_ID = new BreweryIdentifier("sync_rope");
    public static final ResourceLocation SPAWN_KNOT_S2C_ID = new BreweryIdentifier("spawn_knot");
    public static final ResourceLocation SPAWN_COLLISION_S2C_ID = new BreweryIdentifier("spawn_collision");

    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ALCOHOL_SYNC_REQUEST_C2S_ID, new SyncRequestC2SPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DRINK_ALCOHOL_C2S_ID, new DrinkAlcoholC2SPacket());
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ALCOHOL_SYNC_S2C_ID, new AlcoholSyncS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DRUNK_EFFECT_S2C_ID, new DrunkEffectS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DEACTIVATE_KETTLE_S2C_ID, new DeactivateKettleS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ATTACH_ROPE_S2C_ID, new AttachRopeS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DETACH_ROPE_S2C_ID, new DetachRopeS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_ROPE_S2C_ID, new SyncRopeS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SPAWN_KNOT_S2C_ID, new SpawnKnotS2CPacket());
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SPAWN_COLLISION_S2C_ID, new SpawnCollisionS2CPacket());
    }


    public static FriendlyByteBuf createPacketBuf(){
        return new FriendlyByteBuf(Unpooled.buffer());
    }

}
