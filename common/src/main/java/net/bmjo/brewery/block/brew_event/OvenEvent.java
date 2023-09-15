package net.bmjo.brewery.block.brew_event;

import net.bmjo.brewery.block.property.Heat;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class OvenEvent implements BrewEvent {
    @Override
    public void start(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return;
        BlockPos ovenPos = BrewHelper.getOven(components, level);
        if (ovenPos != null) {
            BlockState ovenState = level.getBlockState(ovenPos);
            level.setBlock(ovenPos, ovenState.setValue(BlockStateRegistry.HEAT, Heat.WEAK), 3);
        }
    }

    @Override
    public boolean isFinish(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return true;
        BlockPos ovenPos = BrewHelper.getOven(components, level);
        if (ovenPos != null) {
            BlockState ovenState = level.getBlockState(ovenPos);
            return ovenState.getValue(BlockStateRegistry.HEAT) != Heat.WEAK;
        }
        return true;
    }


}
