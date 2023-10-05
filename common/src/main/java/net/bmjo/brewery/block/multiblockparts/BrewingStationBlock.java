package net.bmjo.brewery.block.multiblockparts;

import net.bmjo.brewery.entity.BrewKettleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class BrewingStationBlock extends HorizontalDirectionalBlock {

    public BrewingStationBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BrewKettleEntity brewKettleEntity = getController(blockPos, level);
        if (brewKettleEntity != null) {
            for (BlockPos pos : brewKettleEntity.getComponents()) {
                if (!pos.equals(blockPos)) {
                    level.removeBlock(pos, false);
                }
            }
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Nullable
    protected BrewKettleEntity getController(BlockPos centerPos, Level level) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                BlockEntity blockEntity = level.getBlockEntity(centerPos.offset(x, 0, y));
                if (blockEntity instanceof BrewKettleEntity brewKettleEntity && brewKettleEntity.isPartOf(centerPos)) {
                    return brewKettleEntity;
                }
            }
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

}
