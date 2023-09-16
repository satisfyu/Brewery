package net.bmjo.brewery.util.rope;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.bmjo.brewery.entity.RopeKnotEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class RopeHelper {
    public static final ObjectList<IncompleteRopeConnection> incompleteRopes = new ObjectArrayList<>(256);

    public static void tick() {
        incompleteRopes.removeIf(IncompleteRopeConnection::tryCompleteOrRemove);
    }

    public static void createConnection(Minecraft client, int fromId, int toIds) {
        createConnections(client, fromId, new int[]{toIds});
    }

    public static void createConnections(Minecraft client, int fromId, int[] toIds) {
        if (client.level == null) return;
        Entity from = client.level.getEntity(fromId);
        if (from instanceof RopeKnotEntity fromKnot) {
            for (int toId : toIds) {
                Entity to = client.level.getEntity(toId);
                if (to == null) {
                    incompleteRopes.add(new IncompleteRopeConnection(fromKnot, toId));
                } else {
                    RopeConnection.create(fromKnot, to);
                }
            }
        }
    }
}
