package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.entity.RopeKnotEntity;
import net.bmjo.brewery.util.rope.IncompleteRopeConnection;
import net.bmjo.brewery.util.rope.RopeConnection;
import net.bmjo.brewery.util.rope.RopeHelper;
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
        if (from instanceof RopeKnotEntity knot) {
            if (to == null) {
                for (IncompleteRopeConnection link : RopeHelper.incompleteRopes) {
                    if (link.from == from && link.toId == toId)
                        link.destroy();
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
}
