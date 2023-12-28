package net.bmjo.brewery.block.brewingstation;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.block.property.BrewMaterial;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BrewWhistleBlock extends BrewingstationBlock {
    public static final BooleanProperty WHISTLE;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    private static final Supplier<VoxelShape> bottomVoxelShapeSupplier;

    private static final Supplier<VoxelShape> topVoxelShapeSupplier;

    public static final Map<Direction, VoxelShape> BOTTOM_SHAPE;

    public static final Map<Direction, VoxelShape> TOP_SHAPE;

    public BrewWhistleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(MATERIAL, BrewMaterial.WOOD).setValue(WHISTLE, false).setValue(HALF, DoubleBlockHalf.LOWER));
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
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if(blockState.getValue(HALF).equals(DoubleBlockHalf.UPPER)) {
            blockPos = blockPos.below();
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        if(blockState.getValue(HALF).equals(DoubleBlockHalf.UPPER)) {
            blockPos = blockPos.below();
        }
        return super.getCloneItemStack(blockGetter, blockPos, blockState);
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Map<Direction, VoxelShape> shapeMap = state.getValue(HALF) == DoubleBlockHalf.LOWER ? BOTTOM_SHAPE : TOP_SHAPE;
        return shapeMap.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MATERIAL, WHISTLE, HALF);
    }

    static {
        WHISTLE = BlockStateRegistry.WHISTLE;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        bottomVoxelShapeSupplier = () -> {
            VoxelShape shape = Shapes.empty();
            shape = Shapes.or(shape, Shapes.box(0, 0.125, 0, 1, 1, 0.125));
            shape = Shapes.or(shape, Shapes.box(0, 0.125, 0.125, 0.125, 1, 1));
            shape = Shapes.or(shape, Shapes.box(0.125, 0, 0.125, 1, 0.125, 1));
            shape = Shapes.or(shape, Shapes.box(0.125, 0.9375, 0.125, 1, 1, 1));
            return shape;
        };
        topVoxelShapeSupplier = () -> {
            VoxelShape shape = Shapes.empty();
            shape = Shapes.or(shape, Shapes.box(0.1875, 0, 0.25, 0.4375, 1, 0.5));
            shape = Shapes.or(shape, Shapes.box(0.125, 0.5, 0.1875, 0.5, 0.5625, 0.5625));
            shape = Shapes.or(shape, Shapes.box(0.125, 0.875, 0.1875, 0.5, 0.9375, 0.5625));
            shape = Shapes.or(shape, Shapes.box(0.125, 0.5, 0.5625, 0.5, 0.75, 0.625));
            shape = Shapes.or(shape, Shapes.box(0.15625, 0.59375, 0.21875, 0.46875, 0.84375, 0.53125));
            return shape;
        };
        BOTTOM_SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                map.put(direction, BreweryUtil.rotateShape(Direction.NORTH, direction, bottomVoxelShapeSupplier.get()));
            }
        });
        TOP_SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                map.put(direction, BreweryUtil.rotateShape(Direction.NORTH, direction, topVoxelShapeSupplier.get()));
            }
        });
    }
}
