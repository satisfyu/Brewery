package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class SyncRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int[] toIds = buf.readVarIntArray();
        context.queue(() -> createLinks(Minecraft.getInstance(), fromId, toIds));
    }

    private void createLinks(Minecraft client, int fromId, int[] toIds) {
        System.out.println("1");
        if (client.level == null) return;
        System.out.println("2");
        Entity from = client.level.getEntity(fromId);
        if (from instanceof HopRopeKnotEntity knot) {
            System.out.println("3");
            for (int toId : toIds) {
                System.out.println(toId);
                Entity to = client.level.getEntity(toId);
                System.out.println(to);
                if (to != null) {
                    System.out.println("4");
                    HopRopeConnection.create(knot, to);
                }
            }
        }
    }
}
