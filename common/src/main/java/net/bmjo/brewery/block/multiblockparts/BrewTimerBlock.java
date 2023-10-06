package net.bmjo.brewery.block.multiblockparts;

import net.bmjo.brewery.registry.BlockStateRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BrewTimerBlock extends BrewingstationBlock {
    public static final BooleanProperty TIME;

    public BrewTimerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TIME, false));
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (blockState.getValue(TIME)) {
            level.setBlock(blockPos, blockState.setValue(TIME, false), 3);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TIME);
    }

    static {
        TIME = BlockStateRegistry.TIME;
    }

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0.125, 0.5, 0.9375, 0.5, 0.875, 1));
        shape = Shapes.or(shape, Shapes.box(0.6875, 0.6875, 0.9375, 0.8125, 0.8125, 1));
        shape = Shapes.or(shape, Shapes.box(0.6875, 0.5, 0.9375, 0.8125, 0.625, 1));
        shape = Shapes.or(shape, Shapes.box(0, 0.125, 0.875, 1, 1, 0.9375));
        shape = Shapes.or(shape, Shapes.box(0.875, 0.125, 0, 1, 1, 0.875));
        shape = Shapes.or(shape, Shapes.box(0, 0, 0, 0.875, 0.125, 0.875));
        shape = Shapes.or(shape, Shapes.box(0, 0.9375, 0, 0.875, 1, 0.875));

        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, BreweryUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }
}
