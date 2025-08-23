package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.core.block.entity.rope.RopeKnotEntity;
import net.satisfy.brewery.core.network.packet.DetachRopeS2CPacket;
import net.satisfy.brewery.core.util.rope.IncompleteRopeConnection;
import net.satisfy.brewery.core.util.rope.RopeConnection;
import net.satisfy.brewery.core.util.rope.RopeHelper;

public class DetachRopeS2CPacketHandler implements NetworkManager.NetworkReceiver<DetachRopeS2CPacket>{

    @Override
    public void receive(DetachRopeS2CPacket packet, NetworkManager.PacketContext context) {
        int fromId = packet.fromId();
        int toId = packet.toId();
        context.queue(() -> removeConnections(Minecraft.getInstance(), fromId, toId));
    }

    private void removeConnections(Minecraft client, int fromId, int toId) {
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
}
