package net.satisfy.brewery.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.block.entity.rope.RopeKnotEntity;
import net.satisfy.brewery.util.BreweryIdentifier;
import net.satisfy.brewery.util.rope.IncompleteRopeConnection;
import net.satisfy.brewery.util.rope.RopeConnection;
import net.satisfy.brewery.util.rope.RopeHelper;
import org.jetbrains.annotations.NotNull;

public record DetachRopeS2CPacket(int fromId, int toId) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_RESOURCE_LOCATION = BreweryIdentifier.of("detach_rope_s2c");
    public static final CustomPacketPayload.Type<DetachRopeS2CPacket> PACKET_ID = new CustomPacketPayload.Type<>(PACKET_RESOURCE_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, DetachRopeS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DetachRopeS2CPacket::fromId,
            ByteBufCodecs.INT, DetachRopeS2CPacket::toId,
            DetachRopeS2CPacket::new
    );

    public static void removeConnections(Minecraft client, int fromId, int toId) {
        if (client.level == null) return;
        Entity from = client.level.getEntity(fromId);
        Entity to = client.level.getEntity(toId);
        if (from instanceof RopeKnotEntity knot) {
            if (to == null) {
                for (IncompleteRopeConnection connection : RopeHelper.incompleteRopes) {
                    if (connection.from == from && connection.toId == toId)
                        connection.destroy();
                }
            } else {
                for (RopeConnection connection : knot.getConnections()) {
                    if (connection.to() == to) {
                        connection.destroy(true);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
