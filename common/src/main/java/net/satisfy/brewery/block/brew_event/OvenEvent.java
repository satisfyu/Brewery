package net.satisfy.brewery.block.brew_event;

import net.satisfy.brewery.block.property.Heat;
import net.satisfy.brewery.registry.BlockStateRegistry;
import net.satisfy.brewery.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class OvenEvent extends BrewEvent {

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
        BlockPos ovenPos = BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), components, level);
        if (ovenPos != null) {
            BlockState ovenState = level.getBlockState(ovenPos);
            level.setBlock(ovenPos, ovenState.setValue(BlockStateRegistry.HEAT, Heat.WEAK), 3);
        }
    }

    @Override
    public boolean isFinish(Set<BlockPos> components, Level level) {
        if (components == null || level == null) return true;
        BlockPos ovenPos = BrewHelper.getBlock(ObjectRegistry.BREW_OVEN.get(), components, level);
        if (ovenPos != null) {
            BlockState ovenState = level.getBlockState(ovenPos);
            return ovenState.getValue(BlockStateRegistry.HEAT) != Heat.WEAK;
        }
        return true;
    }


}
