package net.bmjo.brewery.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BreweryMath {
    public static List<BlockPos> bresenham(HopRopeConnection connection) {
        List<BlockPos> points = new ArrayList<>();
        boolean swapped = false;

        BlockPos firstPoint = connection.from().getPos();
        int x1 = firstPoint.getX();
        int y1 = firstPoint.getY();
        int z1 = firstPoint.getZ();

        BlockPos secondPoint = new BlockPos(connection.to().position());
        int x2 = secondPoint.getX();
        int y2 = secondPoint.getY();
        int z2 = secondPoint.getZ();

        if (Math.abs(x2 - x1) < Math.abs(y2 - y1)) {
            int temp = x1;
            x1 = y1;
            y1 = temp;
            temp = x2;
            x2 = y2;
            y2 = temp;
            swapped = true;
        }

        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
            temp = z1;
            z1 = z2;
            z2 = temp;
            swapped = true;
        }

        int dx = x2 - x1;
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        int error1 = dx / 2;
        int error2 = 0;
        int ystep = (y1 < y2) ? 1 : -1;
        int zstep = (z1 < z2) ? 1 : -1;
        int y = y1;
        int z = z1;

        for (int x = x1; x <= x2; x++) {
            points.add(swapped ? new BlockPos(y, x, z) : new BlockPos(x, y, z));
            error2 += dy;
            if (error2 >= dx) {
                y += ystep;
                error2 -= dx;
            }
            error1 += dz;
            if (error1 >= dx) {
                z += zstep;
                error1 -= dx;
            }
        }

        return points;
    }

    public static boolean isCollinear(BlockPos blockpos, HopRopeConnection connection, double tolerance) {
        BlockPos firstPoint = connection.from().getPos();
        BlockPos secondPoint = new BlockPos(connection.to().position());

        double dx = secondPoint.getX() - firstPoint.getX();
        double dy = secondPoint.getY() - firstPoint.getY();
        double dz = secondPoint.getZ() - firstPoint.getZ();

        double vx = blockpos.getX() - firstPoint.getX();
        double vy = blockpos.getY() - firstPoint.getY();
        double vz = blockpos.getZ() - firstPoint.getZ();

        double crossProductX = vy * dz - vz * dy;
        double crossProductY = vz * dx - vx * dz;
        double crossProductZ = vx * dy - vy * dx;

        return Math.abs(crossProductX) < tolerance && Math.abs(crossProductY) < tolerance && Math.abs(crossProductZ) < tolerance;
    }
}
