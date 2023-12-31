package net.satisfy.brewery.block;

import de.cristelknight.doapi.common.util.ChairUtil;
import net.satisfy.brewery.block.property.LineConnectingType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BenchBlock extends LineConnectingBlock {

    public static final VoxelShape[] TOP_SHAPE;
    public static final VoxelShape[] BOTTOM_SINGLE_SHAPE;
    public static final VoxelShape[] BOTTOM_MULTI_SHAPE;

    public BenchBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        boolean isX = state.getValue(FACING).getAxis() == Direction.Axis.X;
        Direction direction = state.getValue(FACING);

        if (state.getValue(TYPE) == LineConnectingType.NONE) {
            return Shapes.or(isX ? TOP_SHAPE[0] : TOP_SHAPE[1], isX ? BOTTOM_SINGLE_SHAPE[0] : BOTTOM_SINGLE_SHAPE[1]);
        }
        if (state.getValue(TYPE) == LineConnectingType.MIDDLE) {
            return isX ? TOP_SHAPE[0] : TOP_SHAPE[1];
        }

        int i;
        LineConnectingType type = state.getValue(TYPE);

        if ((direction == Direction.NORTH && type == LineConnectingType.LEFT) || (direction == Direction.SOUTH && type == LineConnectingType.RIGHT)) {
            i = 0;
        } else if ((direction == Direction.NORTH && type == LineConnectingType.RIGHT) || (direction == Direction.SOUTH && type == LineConnectingType.LEFT)) {
            i = 1;
        } else if ((direction == Direction.EAST && type == LineConnectingType.RIGHT) || (direction == Direction.WEST && type == LineConnectingType.LEFT)) {
            i = 2;
        } else if ((direction == Direction.EAST && type == LineConnectingType.LEFT) || (direction == Direction.WEST && type == LineConnectingType.RIGHT)) {
            i = 3;
        } else {
            i = 0;
        }
        return Shapes.or(isX ? TOP_SHAPE[0] : TOP_SHAPE[1], BOTTOM_MULTI_SHAPE[i]);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return ChairUtil.onUse(world, player, hand, hit, 0);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        ChairUtil.onStateReplaced(world, pos);
    }

    static {
        TOP_SHAPE = new VoxelShape[]{
                Block.box(2.0, 5.0, 0.0, 14.0, 7.0, 16.0),
                Block.box(0.0, 5.0, 2.0, 16.0, 7.0, 14.0)
        };
        BOTTOM_SINGLE_SHAPE = new VoxelShape[]{
                Shapes.or(Block.box(3.0, 0.0, 2.0, 13.0, 5.0, 4.0), Block.box(3.0, 0.0, 12.0, 13.0, 5.0, 14.0)),
                Shapes.or(Block.box(2.0, 0.0, 3.0, 4.0, 5.0, 13.0), Block.box(12.0, 0.0, 3.0, 14.0, 5.0, 13.0))
        };
        BOTTOM_MULTI_SHAPE = new VoxelShape[]{
                Block.box(2.0, 0.0, 3.0, 4.0, 5.0, 13.0),
                Block.box(12.0, 0.0, 3.0, 14.0, 5.0, 13.0),
                Block.box(3.0, 0.0, 2.0, 13.0, 5.0, 4.0),
                Block.box(3.0, 0.0, 12.0, 13.0, 5.0, 14.0),
        };
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("tooltip.brewery.expandable").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
}
