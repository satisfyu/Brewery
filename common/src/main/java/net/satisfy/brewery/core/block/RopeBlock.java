package net.satisfy.brewery.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class RopeBlock extends Block {
    public static final MapCodec<RopeBlock> CODEC = simpleCodec(RopeBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE_NORTH = Block.box(7, 11, 0, 9, 13, 16);

    public RopeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockPos pos = ctx.getClickedPos();
        LevelReader level = ctx.getLevel();
        return isAnchored(level, pos, facing) ? this.defaultBlockState().setValue(FACING, facing) : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isAnchored(level, pos, state.getValue(FACING));
    }

    private boolean isAnchored(LevelReader level, BlockPos pos, Direction facing) {
        Direction back = facing.getOpposite();
        BlockPos cur = pos.relative(back);

        for (int i = 0; i < 256; i++) {
            BlockState s = level.getBlockState(cur);
            Block b = s.getBlock();

            if (b instanceof RopeKnotBlock) {
                return true;
            }
            if (b instanceof RopeBlock) {
                Direction f = s.getValue(FACING);
                if (f != facing) return false;
                cur = cur.relative(back);
                continue;
            }

            return !s.isAir() && s.isFaceSturdy(level, cur, facing);
        }
        return false;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (dir == facing.getOpposite() || (neighbor.getBlock() instanceof RopeBlock && dir.getAxis() == facing.getAxis())) {
            level.scheduleTick(pos, this, 1);
        }
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return GeneralUtil.rotateShape(Direction.NORTH, state.getValue(FACING), SHAPE_NORTH);
    }
}
