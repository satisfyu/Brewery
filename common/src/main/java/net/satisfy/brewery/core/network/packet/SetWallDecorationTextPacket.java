package net.satisfy.brewery.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.brewery.core.block.entity.WallDecorationBlockEntity;
import net.satisfy.brewery.core.network.BreweryNetworking;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            texts.add(buf.readUtf(50));
        }
        return new SetWallDecorationTextPacket(pos, texts);
    }

    public static void handle(SetWallDecorationTextPacket msg, ServerPlayer player) {
        Level level = player.level();
        if (!level.isLoaded(msg.pos)) {
            return;
        }

        BlockEntity entity = level.getBlockEntity(msg.pos);
        if (!(entity instanceof WallDecorationBlockEntity wallDecorationBlockEntity)) {
            return;
        }

        for (int i = 0; i < msg.texts.size() && i < 3; i++) {
            wallDecorationBlockEntity.setText(i, Component.literal(msg.texts.get(i)));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
