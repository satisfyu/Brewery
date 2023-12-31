package net.satisfy.brewery.block;

import net.satisfy.brewery.entity.StandardBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class BeerStandardWallBlock extends AbstractStandardWallBlock {
    public BeerStandardWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ResourceLocation getRenderTexture() {
        return BeerStandardBlock.TEXTURE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return StandardBlockEntity::tick;
    }
}