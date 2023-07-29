package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.block.Oven;
import net.bmjo.brewery.block.SteamWhistle;
import net.bmjo.brewery.block.Timer;
import net.bmjo.brewery.block.WaterBasin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BrewHelper {
    @Nullable
    public static BlockPos getBasin(Set<BlockPos> components, Level level) {
        for (BlockPos basinPos : components) {
            if (level.getBlockState(basinPos).getBlock() instanceof WaterBasin) {
                return basinPos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getOven(Set<BlockPos> components, Level level) {
        for (BlockPos ovenPos : components) {
            if (level.getBlockState(ovenPos).getBlock() instanceof Oven) {
                return ovenPos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getWhistle(Set<BlockPos> components, Level level) {
        for (BlockPos whistlePos : components) {
            if (level.getBlockState(whistlePos).getBlock() instanceof SteamWhistle) {
                return whistlePos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getTimer(Set<BlockPos> components, Level level) {
        for (BlockPos timerPos : components) {
            if (level.getBlockState(timerPos).getBlock() instanceof Timer) {
                return timerPos;
            }
        }
        return null;
    }
}
