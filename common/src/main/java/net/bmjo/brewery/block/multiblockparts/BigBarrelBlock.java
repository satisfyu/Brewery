package net.bmjo.brewery.block.multiblockparts;

import de.cristelknight.doapi.common.block.FacingBlock;
import net.bmjo.brewery.entity.BrewKettleEntity;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BigBarrelBlock extends FacingBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF;

    public BigBarrelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            level.setBlockAndUpdate(blockPos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER));
        }
    }

    public @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return blockState2.is(this) && blockState2.getValue(HALF) != doubleBlockHalf ? blockState.setValue(FACING, blockState2.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Level level = blockPlaceContext.getLevel();
        BlockPos mainPos = blockPlaceContext.getClickedPos();
        BlockState blockState = super.getStateForPlacement(blockPlaceContext);
        if (blockState == null) return null;
        Direction facing = blockState.getValue(FACING);
        BlockPos backPos = mainPos.relative(facing.getOpposite());
        BlockPos sidePos = mainPos.relative(facing.getCounterClockWise());
        BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
        BlockPos topPos = diagonalPos.above();
        boolean placeable = canPlace(level, backPos, sidePos, diagonalPos, topPos);
        Player player = blockPlaceContext.getPlayer();
        if (!placeable && player != null) {
            player.displayClientMessage(Component.literal("Not enough space").withStyle(ChatFormatting.RED), true);
            player.playSound(SoundEvents.WOOL_HIT);
        }
        return placeable ? blockState : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (level.isClientSide) return;
        Direction facing = blockState.getValue(FACING);
        BlockPos backPos = blockPos.relative(facing.getOpposite());
        BlockPos sidePos = blockPos.relative(facing.getCounterClockWise());
        BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
        BlockPos topPos = diagonalPos.above();
        if (!canPlace(level, backPos, sidePos, diagonalPos, topPos)) return;
        level.setBlock(backPos, ObjectRegistry.BARREL_MAIN_HEAD.get().defaultBlockState().setValue(FACING, facing), 3);
        level.setBlock(sidePos, ObjectRegistry.BARREL_RIGHT.get().defaultBlockState().setValue(FACING, facing), 3);
        level.setBlock(diagonalPos, ObjectRegistry.BARREL_HEAD_RIGHT.get().defaultBlockState().setValue(FACING, facing), 3);
    }

    @Override
    public void onBlockDestroyedByPlayer(BlockState state, Level worldIn, BlockPos pos, Player player) {
        super.onBlockDestroyedByPlayer(state, worldIn, pos, player);

        // Prüfe, ob es sich um einen unteren Block handelt
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos upperPos = pos.above();

            // Zerstöre die oberen Blöcke
            worldIn.destroyBlock(upperPos, true);
            worldIn.destroyBlock(upperPos.relative(FACING), true);
            worldIn.destroyBlock(upperPos.relative(FACING).relative(FACING.getClockWise()), true);
        } else {
            BlockPos lowerPos = pos.below();

            // Zerstöre die unteren Blöcke
            worldIn.destroyBlock(lowerPos, true);
            worldIn.destroyBlock(lowerPos.relative(FACING), true);
            worldIn.destroyBlock(lowerPos.relative(FACING).relative(FACING.getCounterClockWise()), true);
        }
    }
}


    private boolean canPlace(Level level, BlockPos... blockPoses) {
        for (BlockPos blockPos : blockPoses) {
            if (!level.getBlockState(blockPos).isAir()) {
                return false;
            }
        }
        return true;
    }
}
