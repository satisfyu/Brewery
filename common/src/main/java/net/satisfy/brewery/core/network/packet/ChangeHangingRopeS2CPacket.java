package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public record ChangeHangingRopeS2CPacket(int id, boolean active) implements CustomPacketPayload {

    public static final Type<ChangeHangingRopeS2CPacket> TYPE = new Type<>(BreweryNetworking.CHANGE_HANGING_ROPE_S2C_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeHangingRopeS2CPacket> STREAM_CODEC =
            StreamCodec.of(ChangeHangingRopeS2CPacket::toNetwork, ChangeHangingRopeS2CPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, ChangeHangingRopeS2CPacket msg) {
        buf.writeInt(msg.id());
        buf.writeBoolean(msg.active());
    }

    public static ChangeHangingRopeS2CPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        boolean active = buf.readBoolean();
        return new ChangeHangingRopeS2CPacket(id, active);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
