package net.satisfy.brewery.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.util.BreweryIdentifier;
import org.jetbrains.annotations.NotNull;

public record DrunkEffectS2CPacket(boolean activate) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_RESOURCE_LOCATION = BreweryIdentifier.of("drunk_effect_s2c");
    public static final CustomPacketPayload.Type<DrunkEffectS2CPacket> PACKET_ID = new CustomPacketPayload.Type<>(PACKET_RESOURCE_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, DrunkEffectS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, DrunkEffectS2CPacket::activate,
            DrunkEffectS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

