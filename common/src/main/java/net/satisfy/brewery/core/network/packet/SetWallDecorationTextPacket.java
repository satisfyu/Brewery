package net.satisfy.brewery.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.brewery.core.network.BreweryNetworking;

import java.util.List;

public record SetWallDecorationTextPacket(BlockPos pos, List<String> texts) implements CustomPacketPayload {

    public static final Type<SetWallDecorationTextPacket> TYPE = new Type<>(BreweryNetworking.SET_SIGN_TEXT);

    public static final StreamCodec<RegistryFriendlyByteBuf, SetWallDecorationTextPacket> STREAM_CODEC =
            StreamCodec.of(SetWallDecorationTextPacket::toNetwork, SetWallDecorationTextPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf buf, SetWallDecorationTextPacket msg) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.texts.size());
        for (String text : msg.texts) {
            buf.writeUtf(text);
        }
    }

    public static SetWallDecorationTextPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<String> texts = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            texts.add(buf.readUtf(50));
        }
        return new SetWallDecorationTextPacket(pos, texts);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
