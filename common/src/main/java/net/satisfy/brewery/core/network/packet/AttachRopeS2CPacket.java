package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public record AttachRopeS2CPacket(int fromId, int toId) implements CustomPacketPayload {

    public static final Type<AttachRopeS2CPacket> TYPE = new Type<>(BreweryNetworking.ATTACH_ROPE_S2C_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, AttachRopeS2CPacket> STREAM_CODEC =
            StreamCodec.of(AttachRopeS2CPacket::toNetwork, AttachRopeS2CPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, AttachRopeS2CPacket msg) {
        buf.writeInt(msg.fromId());
        buf.writeInt(msg.toId());
    }

    public static AttachRopeS2CPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        int drunkenness = buf.readInt();
        int immunity = buf.readInt();
        return new AttachRopeS2CPacket(drunkenness, immunity);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
