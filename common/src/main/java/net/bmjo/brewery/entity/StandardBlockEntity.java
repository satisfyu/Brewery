package net.bmjo.brewery.entity;

import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StandardBlockEntity extends BlockEntity {
    public StandardBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.STANDARD.get(), blockPos, blockState);
    }
}