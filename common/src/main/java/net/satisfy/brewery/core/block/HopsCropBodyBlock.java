package net.satisfy.brewery.core.block;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HopsCropBodyBlock extends ClimbingCropBlock implements BonemealableBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    public static final IntegerProperty HOPS_AGE = BlockStateProperties.AGE_3;

    public HopsCropBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.defaultBlockState().setValue(HOPS_AGE, 0).setValue(SUPPORTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HOPS_AGE);
    }

    @Override
    protected ItemLike getRipeItem() {
        return ObjectRegistry.HOPS.get();
    }

    @Override
    protected ItemLike getRottenItem() {
        return null;
    }

    @Override
    protected int getHarvestResetAge(Level level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).is(ObjectRegistry.HOPS_CROP.get()) ? 2 : 1;
    }

    private Optional<BlockPos> getHeadPos(LevelReader level, BlockPos pos, BlockState state) {
        return BlockUtil.getTopConnectedBlock(level, pos, state.getBlock(), Direction.UP, ObjectRegistry.HOPS_CROP.get());
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ObjectRegistry.HOPS_CROP.get());
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        boolean base = super.canBeReplaced(state, ctx);
        return (!base || !ctx.getItemInHand().is(ObjectRegistry.HOPS_CROP.get().asItem())) && base;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP) {
            boolean inherit = neighbor.getBlock() instanceof ClimbingCropBlock && neighbor.hasProperty(SUPPORTED) && neighbor.getValue(SUPPORTED);
            state = state.setValue(SUPPORTED, state.getValue(SUPPORTED) || inherit);
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
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
        Optional<BlockPos> head = getHeadPos(level, pos, state);
        if (head.isPresent()) {
            BlockPos hp = head.get();
            if (HopsCropHeadBlock.getHeight(hp, level) < HopsCropHeadBlock.getMaxHeight(level, hp) && level.getBlockState(hp.above()).isAir()) {
                BlockState headState = level.getBlockState(hp);
                boolean supported = headState.hasProperty(SUPPORTED) && headState.getValue(SUPPORTED);
                BlockState body = ObjectRegistry.HOPS_CROP_BODY.get().defaultBlockState()
                        .setValue(HopsCropHeadBlock.HOPS_AGE, headState.getValue(HopsCropHeadBlock.HOPS_AGE))
                        .setValue(SUPPORTED, supported);
                level.setBlock(hp, body, 2);
                level.setBlockAndUpdate(hp.above(), ObjectRegistry.HOPS_CROP.get().defaultBlockState().setValue(SUPPORTED, supported));
                return;
            }
        }
        if (state.getValue(HOPS_AGE) < 3) {
            boolean supported = state.getValue(SUPPORTED);
            level.setBlockAndUpdate(pos, state.setValue(HOPS_AGE, state.getValue(HOPS_AGE) + 1).setValue(SUPPORTED, supported));
        } else {
            dropFruits(level, pos, state);
        }
    }
}
