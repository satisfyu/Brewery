package net.bmjo.brewery.block.crops;

import net.bmjo.brewery.entity.rope.HangingRopeEntity;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class HopsCropBlock extends Block {
    protected final VoxelShape shape;
    public static final IntegerProperty AGE;
    private static final int MAX_AGE = 3;

    protected HopsCropBlock(BlockBehaviour.Properties arg, VoxelShape shape) {
        super(arg);
        this.shape = shape;
    }

    public static HopsCropHeadBlock getHeadBlock() {
        return (HopsCropHeadBlock) ObjectRegistry.HOPS_CROP.get();
    }

    public static HopsCropBodyBlock getBodyBlock() {
        return (HopsCropBodyBlock) ObjectRegistry.HOPS_CROP_BODY.get();
    }

    protected static boolean isRopeAbove(LevelAccessor levelAccessor, BlockPos blockPos) {
        List<HangingRopeEntity> results = levelAccessor.getEntitiesOfClass(HangingRopeEntity.class, new AABB(blockPos.above(), blockPos.above().offset(1, HangingRopeEntity.MAX_LENGTH, 1)));
        for (HangingRopeEntity hangingRope : results) {
            if (hangingRope.active()) return true;
        }
        return false;
    }

    protected static int getHeight(BlockPos blockPos, LevelAccessor levelAccessor) {
        int height = 0;
        while (levelAccessor.getBlockState(blockPos.below(height)).getBlock() instanceof HopsCropBlock) {
            height++;
        }
        return height;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().above());
        return !blockState.is(getHeadBlock()) && !blockState.is(getBodyBlock()) ? this.defaultBlockState() : getBodyBlock().defaultBlockState();
    }

    protected BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(AGE, Math.min(age, MAX_AGE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos belowPos = blockPos.relative(Direction.DOWN);
        BlockState belowState = levelReader.getBlockState(belowPos);
        return belowState.is(getHeadBlock()) || belowState.is(getBodyBlock()) || belowState.is(Blocks.FARMLAND);
    }

    protected boolean canGrow(BlockState blockState) {
        return blockState.getValue(AGE) < MAX_AGE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (player.getItemInHand(interactionHand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        int age = blockState.getValue(AGE);
        if (age > 1) {
            dropHops(level, blockPos, blockState);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
        }
    }

    protected void dropHops(Level level, BlockPos blockPos, BlockState blockState) {
        int age = blockState.getValue(AGE);
        int amount = level.getRandom().nextInt(2);
        popResource(level, blockPos, new ItemStack(ObjectRegistry.HOPS.get(), amount + (age >= MAX_AGE ? 1 : 0)));
        level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        level.setBlock(blockPos, blockState.setValue(AGE, 1), 2);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return canGrow(blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
            int age = blockState.getValue(AGE);
            if (age < MAX_AGE) {
                if (randomSource.nextFloat() < 0.2) {
                    serverLevel.setBlock(blockPos, this.getStateForAge(age + 1), 2);
                }
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE = BlockStateProperties.AGE_3;
    }
}
