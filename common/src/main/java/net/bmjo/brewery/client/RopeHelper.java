package net.bmjo.brewery.client;

import com.mojang.math.Vector3f;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RopeHelper {

    public static void createLink(Minecraft client, int fromId, int toIds) {
        createLinks(client, fromId, new int[]{toIds});
    }

    public static void createLinks(Minecraft client, int fromId, int[] toIds) {
        if (client.level == null) return;
        Entity from = client.level.getEntity(fromId);
        if (from instanceof HopRopeKnotEntity fromKnot) {
            for (int toId : toIds) {
                Entity to = client.level.getEntity(toId);
                if (to != null) {
                    HopRopeConnection.create(fromKnot, to);
                } else {
                    System.out.println("ID is wrong: " + toId);
                }
            }
        }
    }

    public static Vec3 getChainOffset(Vec3 start, Vec3 end) {
        Vec3 vec = end.subtract(start);
        Vector3f offset = new Vector3f((float)vec.x(), 0.0F, (float)vec.z());
        offset.normalize();
        offset.mul(2 / 16F);
        return new Vec3(offset.x(), offset.y(), offset.z());
    }

    private static final double HANGING_AMOUNT = 50.0F;

    public static double drip2(double x, double d, double h) {
        double a = HANGING_AMOUNT; // 7
        double p1 = a * asinh((h / (2D * a)) * (1D / Math.sinh(d / (2D * a))));
        double p2 = -a * Math.cosh((2D * p1 - d) / (2D * a));
        return p2 + a * Math.cosh((((2D * x) + (2D * p1)) - d) / (2D * a));
    }

    public static double drip2prime(double x, double d, double h) {
        double a = HANGING_AMOUNT;
        double p1 = a * asinh((h / (2D * a)) * (1D / Math.sinh(d / (2D * a))));
        return Math.sinh((2 * x + 2 * p1 - d) / (2 * a));
    }

    private static double asinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }
}
