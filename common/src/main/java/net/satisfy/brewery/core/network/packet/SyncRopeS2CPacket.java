package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public record SyncRopeS2CPacket(int fromId, int[] toIds) implements CustomPacketPayload {

    public static final Type<SyncRopeS2CPacket> TYPE = new Type<>(BreweryNetworking.SYNC_ROPE_S2C_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRopeS2CPacket> STREAM_CODEC =
            StreamCodec.of(SyncRopeS2CPacket::toNetwork, SyncRopeS2CPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, SyncRopeS2CPacket msg) {
        buf.writeInt(msg.fromId());
        buf.writeVarIntArray(msg.toIds());
    }

    public static SyncRopeS2CPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        int fromId = buf.readInt();
        int[] toIds = buf.readVarIntArray();
        return new SyncRopeS2CPacket(fromId, toIds);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

