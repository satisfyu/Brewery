package net.satisfy.brewery.core.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.brewery.core.block.entity.WallDecorationBlockEntity;

import java.util.List;

public class SetWallDecorationTextPacket {
    private final BlockPos pos;
    private final List<String> texts;

    public SetWallDecorationTextPacket(BlockPos pos, List<String> texts) {
        this.pos = pos;
        this.texts = texts;
    }

    public static void encode(SetWallDecorationTextPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.texts.size());
        for (String text : msg.texts) {
            buf.writeUtf(text);
        }
    }

    public static SetWallDecorationTextPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<String> texts = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            texts.add(buf.readUtf(50));
        }
        return new SetWallDecorationTextPacket(pos, texts);
    }

    public static void handle(SetWallDecorationTextPacket msg, ServerPlayer player) {
        Level level = player.level();
        if (level.isLoaded(msg.pos)) {
            BlockEntity entity = level.getBlockEntity(msg.pos);
            if (entity instanceof WallDecorationBlockEntity signEntity) {
                for (int i = 0; i < msg.texts.size() && i < 4; i++) {
                    signEntity.setText(i, Component.literal(msg.texts.get(i)));
                }
            }
        }
    }
}
