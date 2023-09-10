package net.bmjo.brewery.client;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class HopRopeHelper {
    public static Vec3 getChainOffset(Vec3 start, Vec3 end) {
        Vec3 vec = end.subtract(start);
        Vector3f offset = new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z());
        offset.set(offset.x(), 0, offset.z());
        offset.normalize();
        offset.normalize(2 / 16f);
        return new Vec3(offset.x(), offset.y(), offset.z());
    }

    private static final double HANGING_AMOUNT = 50.0F;

    public static double drip2(double x, double d, double h) {
        double a = HANGING_AMOUNT;
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
