package net.satisfy.brewery.block.brew_event;

import net.satisfy.brewery.entity.BrewstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Set;

public abstract class BrewEvent {

    private int timeLeft;

    public void tick(BrewstationBlockEntity entity){
        onTick(entity);
        timeLeft--;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeForEvent(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public abstract CompoundTag save(CompoundTag compoundTag);

    public abstract void load(CompoundTag compoundTag);

    public void onTick(BrewstationBlockEntity entity) {

    }

    protected BrewEvent() {
        this(0);
    }

    protected BrewEvent(int time){
        timeLeft = time;
    }

    public abstract void start(Set<BlockPos> components, Level level);

    public abstract boolean isFinish(Set<BlockPos> components, Level level);

    public void finish(Set<BlockPos> components, Level level) {

    }
}
