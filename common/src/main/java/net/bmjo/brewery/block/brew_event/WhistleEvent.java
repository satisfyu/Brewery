package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.block.property.Liquid;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class WhistleEvent implements BrewEvent {
    @Override
    public void start(Set<BlockPos> components, Level level) {
        if (components.isEmpty() || level == null) return;
        BlockPos basinPos = BrewHelper.getBlock(ObjectRegistry.WOODEN_BREWINGSTATION.get(), components, level);
        BlockPos whistlePos = BrewHelper.getBlock(ObjectRegistry.BREW_WHISTLE.get(), components, level);
        if (basinPos != null && whistlePos != null) {
            BlockState basinState = level.getBlockState(basinPos);
            BlockState whistleState = level.getBlockState(whistlePos);
            level.setBlock(basinPos, basinState.setValue(BlockStateRegistry.LIQUID, Liquid.DRAINED), 3);
            level.setBlock(whistlePos, whistleState.setValue(BlockStateRegistry.WHISTLE, true), 3);
        }
    }

    @Override
    public boolean isFinish(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return true;
        BlockPos basinPos = BrewHelper.getBlock(ObjectRegistry.WOODEN_BREWINGSTATION.get(), components, level);
        if (basinPos != null) {
            BlockState basinState = level.getBlockState(basinPos);
            return basinState.getValue(BlockStateRegistry.LIQUID) == Liquid.DRAINED;
        }
        return true;
    }

    @Override
    public void finish(Set<BlockPos> components, Level level) {
        BlockPos whistlePos = BrewHelper.getBlock(ObjectRegistry.BREW_WHISTLE.get(), components, level);
        if (whistlePos != null) {
            BlockState whistleState = level.getBlockState(whistlePos);
            level.setBlock(whistlePos, whistleState.setValue(BlockStateRegistry.WHISTLE, false), 3);
        }
    }
}
