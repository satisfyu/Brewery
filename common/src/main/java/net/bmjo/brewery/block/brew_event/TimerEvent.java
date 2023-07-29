package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.block.property.BlockStateRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class TimerEvent implements BrewEvent {
    @Override
    public void start(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return;
        BlockPos timerPos = BrewHelper.getTimer(components, level);
        if (timerPos != null) {
            BlockState timerState = level.getBlockState(timerPos);
            level.setBlock(timerPos, timerState.setValue(BlockStateRegistry.TIME, true), 3);
        }
    }

    @Override
    public boolean isFinish(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return true;
        BlockPos timerPos = BrewHelper.getTimer(components, level);
        if (timerPos != null) {
            BlockState timerState = level.getBlockState(timerPos);
            if (timerState.getValue(BlockStateRegistry.TIME)) {
                return false;
            }
        }
        return true;
    }


}
