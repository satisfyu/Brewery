package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public record SyncRequestC2SPacket() implements CustomPacketPayload {

    public static final Type<SyncRequestC2SPacket> TYPE = new Type<>(BreweryNetworking.ALCOHOL_SYNC_REQUEST_C2S_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRequestC2SPacket> STREAM_CODEC =
            StreamCodec.of(SyncRequestC2SPacket::toNetwork, SyncRequestC2SPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, SyncRequestC2SPacket msg) {
    }

    public static SyncRequestC2SPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        return new SyncRequestC2SPacket();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
