package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class AttachRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int toId = buf.readInt();
        context.queue(() -> createLinks(Minecraft.getInstance(), fromId, toId));
    }

    private void createLinks(Minecraft client, int fromId, int toIds) {
        if (client.level == null) return;
        Entity from = client.level.getEntity(fromId);
        if (from instanceof HopRopeKnotEntity knot) {
            Entity to = client.level.getEntity(toIds);
            if (to != null) {
                HopRopeConnection.create(knot, to);
            }
        }
    }
}
