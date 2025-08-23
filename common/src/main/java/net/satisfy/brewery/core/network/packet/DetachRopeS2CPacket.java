package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public record DetachRopeS2CPacket(int fromId, int toId) implements CustomPacketPayload {

    public static final Type<DetachRopeS2CPacket> TYPE = new Type<>(BreweryNetworking.DETACH_ROPE_S2C_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, DetachRopeS2CPacket> STREAM_CODEC =
            StreamCodec.of(DetachRopeS2CPacket::toNetwork, DetachRopeS2CPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, DetachRopeS2CPacket msg) {
        buf.writeInt(msg.fromId());
        buf.writeInt(msg.toId());
    }

    public static DetachRopeS2CPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        int fromId = buf.readInt();
        int toId = buf.readInt();
        return new DetachRopeS2CPacket(fromId, toId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
