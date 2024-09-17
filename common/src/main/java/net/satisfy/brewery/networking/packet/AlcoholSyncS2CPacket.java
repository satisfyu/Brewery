package net.satisfy.brewery.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.util.BreweryIdentifier;
import org.jetbrains.annotations.NotNull;

public record AlcoholSyncS2CPacket(int drunkenness, int immunity) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_RESOURCE_LOCATION = BreweryIdentifier.of("alcohol_sync_s2c");
    public static final CustomPacketPayload.Type<AlcoholSyncS2CPacket> PACKET_ID = new CustomPacketPayload.Type<>(PACKET_RESOURCE_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, AlcoholSyncS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AlcoholSyncS2CPacket::drunkenness,
            ByteBufCodecs.INT, AlcoholSyncS2CPacket::immunity,
            AlcoholSyncS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
