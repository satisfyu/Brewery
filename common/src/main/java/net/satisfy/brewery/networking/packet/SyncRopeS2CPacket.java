package net.satisfy.brewery.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.util.BreweryIdentifier;
import net.satisfy.brewery.util.CodecUtil;
import org.jetbrains.annotations.NotNull;

public record SyncRopeS2CPacket(int fromId, int[] toIds) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_RESOURCE_LOCATION = BreweryIdentifier.of("sync_rope_s2c");
    public static final CustomPacketPayload.Type<SyncRopeS2CPacket> PACKET_ID = new CustomPacketPayload.Type<>(PACKET_RESOURCE_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRopeS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncRopeS2CPacket::fromId,
            CodecUtil.INT_ARRAY, SyncRopeS2CPacket::toIds,
            SyncRopeS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
