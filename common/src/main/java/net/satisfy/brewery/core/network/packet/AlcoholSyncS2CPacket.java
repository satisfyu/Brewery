package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;
import org.jetbrains.annotations.NotNull;

public record AlcoholSyncS2CPacket(int drunkenness, int immunity) implements CustomPacketPayload {

    public static final Type<AlcoholSyncS2CPacket> TYPE = new Type<>(BreweryNetworking.ALCOHOL_SYNC_S2C_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, AlcoholSyncS2CPacket> STREAM_CODEC =
            StreamCodec.of(AlcoholSyncS2CPacket::toNetwork, AlcoholSyncS2CPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, AlcoholSyncS2CPacket msg) {
        buf.writeInt(msg.drunkenness());
        buf.writeInt(msg.immunity());
    }

    public static AlcoholSyncS2CPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        int drunkenness = buf.readInt();
        int immunity = buf.readInt();
        return new AlcoholSyncS2CPacket(drunkenness, immunity);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
