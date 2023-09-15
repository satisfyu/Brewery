package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.block.multiblockparts.OvenBlock;
import net.bmjo.brewery.block.multiblockparts.SteamWhistleBlock;
import net.bmjo.brewery.block.multiblockparts.TimerBlock;
import net.bmjo.brewery.block.multiblockparts.WaterBasinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BrewHelper {
    @Nullable
    public static BlockPos getBasin(Set<BlockPos> components, Level level) {
        for (BlockPos basinPos : components) {
            if (level.getBlockState(basinPos).getBlock() instanceof WaterBasinBlock) {
                return basinPos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getOven(Set<BlockPos> components, Level level) {
        for (BlockPos ovenPos : components) {
            if (level.getBlockState(ovenPos).getBlock() instanceof OvenBlock) {
                return ovenPos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getWhistle(Set<BlockPos> components, Level level) {
        for (BlockPos whistlePos : components) {
            if (level.getBlockState(whistlePos).getBlock() instanceof SteamWhistleBlock) {
                return whistlePos;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos getTimer(Set<BlockPos> components, Level level) {
        for (BlockPos timerPos : components) {
            if (level.getBlockState(timerPos).getBlock() instanceof TimerBlock) {
                return timerPos;
            }
        }
        return null;
    }
}
