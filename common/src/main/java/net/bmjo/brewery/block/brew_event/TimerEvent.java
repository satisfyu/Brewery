package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.registry.BlockStateRegistry;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class TimerEvent extends BrewEvent {
    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {

    }

    @Override
    public void start(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return;
        BlockPos timerPos = BrewHelper.getBlock(ObjectRegistry.BREW_TIMER.get(), components, level);
        if (timerPos != null) {
            BlockState timerState = level.getBlockState(timerPos);
            level.setBlock(timerPos, timerState.setValue(BlockStateRegistry.TIME, true), 3);
        }
    }

    @Override
    public boolean isFinish(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return true;
        BlockPos timerPos = BrewHelper.getBlock(ObjectRegistry.BREW_TIMER.get(), components, level);
        if (timerPos != null) {
            BlockState timerState = level.getBlockState(timerPos);
            return !timerState.getValue(BlockStateRegistry.TIME);
        }
        return true;
    }


}
