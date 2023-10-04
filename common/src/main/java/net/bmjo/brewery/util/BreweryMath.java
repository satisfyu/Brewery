package net.bmjo.brewery.util;

import net.bmjo.brewery.entity.rope.RopeKnotEntity;
import net.bmjo.brewery.util.rope.RopeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class BreweryMath {
    public static BlockPos ofFloored(final Vec3 vec) {
        return ofFloored(vec.x(), vec.y(), vec.z());
    }

    public static BlockPos ofFloored(final double x, final double y, final double z) {
        return new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    public static Set<BlockPos> lineIntersection(RopeConnection connection) {
        if (connection.to() instanceof RopeKnotEntity toKnot) {
            BlockPos start = connection.from().getPos();
            BlockPos end = toKnot.getPos();
            return lineIntersection(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
        }
        return new HashSet<>();
    }

    private static Set<BlockPos> lineIntersection(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        Set<BlockPos> blockPositions = new HashSet<>();
        if (endX - startX == 0 && endZ - startZ == 0) {
            return blockPositions;
        }

        boolean switchX = false;
        if (startX > endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
            switchX = true;
        }

        boolean switchY = false;
        if (startY > endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
            switchY = true;
        }

        boolean switchZ = false;
        if (startZ > endZ) {
            int temp = startZ;
            startZ = endZ;
            endZ = temp;
            switchZ = true;
        }

        int dx = endX - startX;
        int dy = endY - startY;
        int dz = endZ - startZ;

        // Calculate the greatest common divisor (GCD) of the direction components
        int gcd = gcd(gcd(dx, dy), dz);

        if (gcd == 0) {
            return blockPositions;
        }

        // Iterate over t values within the range
        for (int t = 1; t < gcd; t++) {
            int x = switchX ? endX - (dx * t) / gcd : startX + (dx * t) / gcd;
            int y = switchY ? endY - (dy * t) / gcd : startY + (dy * t) / gcd;
            int z = switchZ ? endZ - (dz * t) / gcd : startZ + (dz * t) / gcd;

            blockPositions.add(new BlockPos(x, y, z));
        }
        return blockPositions;
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public static Vec3 middleOf(Vec3 a, Vec3 b) {
        double x = (a.x() - b.x()) / 2d + b.x();
        double y = (a.y() - b.y()) / 2d + b.y();
        double z = (a.z() - b.z()) / 2d + b.z();
        return new Vec3(x, y, z);
    }
}
