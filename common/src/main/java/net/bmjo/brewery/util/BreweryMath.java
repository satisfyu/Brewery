package net.bmjo.brewery.util;

import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BreweryMath {
    public static List<BlockPos> lineIntersection(HopRopeConnection connection) {
        if (connection.to() instanceof HopRopeKnotEntity toKnot) {
            BlockPos start = connection.from().getPos();
            BlockPos end = toKnot.getOnPos();
            return lineIntersection(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
        }
        return new ArrayList<>();
    }

    private static List<BlockPos> lineIntersection(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        List<BlockPos> blockPositions = new ArrayList<>();

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

        // Iterate over t values within the range
        for (int t = 0; t <= gcd; t++) {
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
