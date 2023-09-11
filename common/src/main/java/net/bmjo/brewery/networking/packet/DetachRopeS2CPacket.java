package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class DetachRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int toId = buf.readInt();
        context.queue(() -> removeLinks(Minecraft.getInstance(), fromId, toId));
    }

    private void removeLinks(Minecraft client, int fromId, int toId) {
        if (client.level == null) return;
        Entity from = client.level.getEntity(fromId);
        Entity to = client.level.getEntity(toId);
        if (from instanceof HopRopeKnotEntity knot) {
            if (to != null) {
                for (HopRopeConnection connection : knot.getConnections()) {
                    if (connection.second() == to) {
                        connection.destroy();
                    }
                }
            }
        }
    }
}
