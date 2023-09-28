package net.bmjo.brewery.util.rope;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.bmjo.brewery.entity.RopeKnotEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

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

    public static double  getYHanging(double d, Vec3 ropeVec) {
        if (ropeVec.x == 0 && ropeVec.z == 0) {
            return 0;
        }
        double dXZ = Math.sqrt(ropeVec.x * ropeVec.x + ropeVec.z * ropeVec.z);
        double a, p;
        a = 1 - 1 / (0.1D * dXZ + 1);
        p = Math.PI / dXZ;
        return -a * Math.sin(p * d);
    }
}
