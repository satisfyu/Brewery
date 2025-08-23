package net.satisfy.brewery.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

public class DrinkAlcoholC2SPacket implements CustomPacketPayload {

    public static final Type<DrinkAlcoholC2SPacket> TYPE = new Type<>(BreweryNetworking.DRINK_ALCOHOL_C2S_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkAlcoholC2SPacket> STREAM_CODEC =
            StreamCodec.of(DrinkAlcoholC2SPacket::toNetwork, DrinkAlcoholC2SPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, DrinkAlcoholC2SPacket msg) {
    }

    public static DrinkAlcoholC2SPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        return new DrinkAlcoholC2SPacket();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
