package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public record DrunkEffectS2CPacket(boolean activate) implements CustomPacketPayload {

    public static final Type<DrunkEffectS2CPacket> TYPE = new Type<>(BreweryNetworking.DRUNK_EFFECT_S2C_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, DrunkEffectS2CPacket> STREAM_CODEC =
            StreamCodec.of(DrunkEffectS2CPacket::toNetwork, DrunkEffectS2CPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, DrunkEffectS2CPacket msg) {
    }

    public static DrunkEffectS2CPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        boolean activate = buf.readBoolean();
        return new DrunkEffectS2CPacket(activate);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

