package net.satisfy.brewery.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.jetbrains.annotations.NotNull;

public class HopsCropHeadBlock extends ClimbingCropBlock implements BonemealableBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    public static final IntegerProperty HOPS_AGE = BlockStateProperties.AGE_3;

    public HopsCropHeadBlock(Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.defaultBlockState().setValue(HOPS_AGE, 0).setValue(SUPPORTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HOPS_AGE);
    }

    @Override
    public @NotNull BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(HOPS_AGE, Math.min(age, 3)).setValue(SUPPORTED, false);
    }

    @Override
    protected ItemLike getRipeItem() {
        return ObjectRegistry.HOPS.get();
    }

    @Override
    protected ItemLike getRottenItem() {
        return null;
    }

    public static int getMaxHeight(LevelAccessor level, BlockPos pos) {
        BlockState self = level.getBlockState(pos);
        boolean supported = self.hasProperty(SUPPORTED) && self.getValue(SUPPORTED);
        return supported ? 4 : 2;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (getHeight(pos, level) > getMaxHeight(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (level.getRawBrightness(pos, 0) >= 9 && random.nextFloat() < 0.2F) {
            if (getHeight(pos, level) < getMaxHeight(level, pos) && level.getBlockState(pos.above()).isAir()) {
                boolean supported = state.getValue(SUPPORTED);
                BlockState body = ObjectRegistry.HOPS_CROP_BODY.get().defaultBlockState()
                        .setValue(HOPS_AGE, state.getValue(HOPS_AGE))
                        .setValue(SUPPORTED, supported);
                level.setBlock(pos, body, 2);
                level.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(SUPPORTED, supported));
                return;
            }
            if (state.getValue(HOPS_AGE) < 3) {
                boolean supported = state.getValue(SUPPORTED);
                level.setBlockAndUpdate(pos, state.setValue(HOPS_AGE, state.getValue(HOPS_AGE) + 1).setValue(SUPPORTED, supported));
            }
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP) {
            boolean inherit = neighbor.getBlock() instanceof ClimbingCropBlock && neighbor.hasProperty(SUPPORTED) && neighbor.getValue(SUPPORTED);
            state = state.setValue(SUPPORTED, state.getValue(SUPPORTED) || inherit);
        }
        if (dir == Direction.DOWN && !state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return state;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (getHeight(pos, level) < getMaxHeight(level, pos) && level.getBlockState(pos.above()).isAir()) {
            boolean supported = state.getValue(SUPPORTED);
            BlockState body = ObjectRegistry.HOPS_CROP_BODY.get().defaultBlockState()
                    .setValue(HOPS_AGE, state.getValue(HOPS_AGE))
                    .setValue(SUPPORTED, supported);
            level.setBlock(pos, body, 2);
            level.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(SUPPORTED, supported));
            return;
        }
        if (state.getValue(HOPS_AGE) < 3) {
            boolean supported = state.getValue(SUPPORTED);
            level.setBlockAndUpdate(pos, state.setValue(HOPS_AGE, state.getValue(HOPS_AGE) + 1).setValue(SUPPORTED, supported));
        } else {
            dropFruits(level, pos, state);
        }
    }
}