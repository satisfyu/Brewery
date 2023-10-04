package net.bmjo.brewery.block.brew_event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BrewHelper {
    @Nullable
    public static BlockPos getBlock(Block block, Set<BlockPos> components, Level level) {
        for (BlockPos pos : components) {
            if (level.getBlockState(pos).getBlock() == block) {
                return pos;
            }
        }
        return null;
    }
}
