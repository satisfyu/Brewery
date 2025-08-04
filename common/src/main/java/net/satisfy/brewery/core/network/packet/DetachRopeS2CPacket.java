package net.satisfy.brewery.core.network.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.core.block.entity.rope.RopeKnotEntity;
import net.satisfy.brewery.core.util.rope.IncompleteRopeConnection;
import net.satisfy.brewery.core.util.rope.RopeConnection;
import net.satisfy.brewery.core.util.rope.RopeHelper;

public class DetachRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int toId = buf.readInt();
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
