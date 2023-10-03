package net.bmjo.brewery.block.crops;

import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HopsCropBodyBlock extends HopsCropBlock implements BonemealableBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public HopsCropBodyBlock(BlockBehaviour.Properties arg) {
        super(arg, SHAPE);
    }

    private Optional<BlockPos> getHeadPos(BlockGetter blockGetter, BlockPos blockPos, Block block) {
        return BlockUtil.getTopConnectedBlock(blockGetter, blockPos, block, Direction.UP, ObjectRegistry.HOPS_CROP.get());
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(getHeadBlock());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        boolean bl = blockState.canBeReplaced(blockPlaceContext);
        return (!bl || !blockPlaceContext.getItemInHand().is(getHeadBlock().asItem())) && bl;
    }

    @SuppressWarnings("deprecation")
    public @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }
        HopsCropHeadBlock hopsCropHeadBlock = getHeadBlock();
        if (direction == Direction.UP && !blockState2.is(this) && !blockState2.is(hopsCropHeadBlock)) {
            if (getHeight(blockPos, levelAccessor) > 2 && !isRopeAbove(levelAccessor, blockPos)) {
                levelAccessor.scheduleTick(blockPos, hopsCropHeadBlock, 1);
            }
            return hopsCropHeadBlock.getStateForAge(blockState.getValue(AGE));
        } else {
            return blockState;
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean bl) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        Optional<BlockPos> optional = this.getHeadPos(serverLevel, blockPos, blockState.getBlock());
        if (optional.isPresent()) {
            BlockPos pos = optional.get();
            if (HopsCropHeadBlock.canGrowInto(serverLevel, pos.above())) {
                serverLevel.setBlockAndUpdate(pos.above(), ObjectRegistry.HOPS_CROP.get().defaultBlockState());
                return;
            }
        }
        if (this.canGrow(blockState)) {
            serverLevel.setBlockAndUpdate(blockPos, getStateForAge(blockState.getValue(AGE) + 1));
        } else {
            dropHops(serverLevel, blockPos, blockState);
        }
    }
}