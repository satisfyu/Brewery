package net.bmjo.brewery.block.brew_event;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Set;

public abstract class BrewEvent {
    private final ResourceLocation id;

    protected BrewEvent(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation id() {
        return this.id;
    }

    public abstract void start(Set<BlockPos> components, Level level);

    public abstract boolean isFinish(Set<BlockPos> components, Level level);

    public void finish(Set<BlockPos> components, Level level) {

    }
}
