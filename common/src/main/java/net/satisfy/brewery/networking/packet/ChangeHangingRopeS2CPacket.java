package net.satisfy.brewery.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.util.BreweryIdentifier;
import org.jetbrains.annotations.NotNull;

public record ChangeHangingRopeS2CPacket(int id, boolean active) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_RESOURCE_LOCATION = BreweryIdentifier.of("change_hanging_rope_s2c");
    public static final CustomPacketPayload.Type<ChangeHangingRopeS2CPacket> PACKET_ID = new CustomPacketPayload.Type<>(PACKET_RESOURCE_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeHangingRopeS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ChangeHangingRopeS2CPacket::id,
            ByteBufCodecs.BOOL, ChangeHangingRopeS2CPacket::active,
            ChangeHangingRopeS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
