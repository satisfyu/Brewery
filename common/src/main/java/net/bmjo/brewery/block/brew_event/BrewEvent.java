package net.bmjo.brewery.block.brew_event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface BrewEvent {
    void start(Set<BlockPos> components, Level level);
    boolean isFinish(Set<BlockPos> components, Level level);
    default void finish(Set<BlockPos> components, Level level) {

    }
}
