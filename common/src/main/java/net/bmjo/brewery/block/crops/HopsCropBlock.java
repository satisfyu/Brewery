package net.bmjo.brewery.block.crops;

import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class HopsCropBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 4);
    protected final Direction growthDirection;

    public HopsCropBlock(Properties properties, Direction growthDirection) {
        super(properties);
        this.growthDirection = growthDirection;
    }

    protected BlockState updateHeadAfterConvertedFromBody(BlockState blockState, BlockState blockState2) {
        return blockState2;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().relative(this.growthDirection));
        if (blockState.is(this.getHeadBlock()) || blockState.is(this.getBodyBlock())) {
            return this.getBodyBlock().defaultBlockState();
        }
        return this.getStateForPlacement(blockPlaceContext.getLevel());
    }

    public BlockState getStateForPlacement(LevelAccessor levelAccessor) {
        return this.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.relative(this.growthDirection.getOpposite());
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        if (!this.canAttachTo(blockState2)) {
            return false;
        }
        return blockState2.is(this.getHeadBlock()) || blockState2.is(this.getBodyBlock()) || blockState2.isFaceSturdy(levelReader, blockPos2, this.growthDirection);
    }

    protected boolean canAttachTo(BlockState blockState) {
        return true;
    }

    protected GrowingPlantHeadBlock getHeadBlock() {
        return ObjectRegistry.HOPS_CROP_HEAD.get();
    }
    protected Block getBodyBlock() {
        return ObjectRegistry.HOPS_CROP_BODY.get();
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ObjectRegistry.CORN_SEEDS.get();
    }

    @Override
    public int getMaxAge() {
        return 4;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

}
