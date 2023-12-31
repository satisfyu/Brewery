package net.satisfy.brewery.block;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.entity.SideBoardBlockEntity;
import net.satisfy.brewery.registry.BlockEntityRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SideBoardBlock extends ChestBlock {

    public static final Map<Direction, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        map.put(Direction.NORTH, Block.box(0,0,4, 16, 16, 16));
        map.put(Direction.SOUTH, Block.box(0,0,0, 16, 16, 12));
        map.put(Direction.WEST, Block.box(4,0,0, 16, 16, 16));
        map.put(Direction.EAST, Block.box(0,0,0, 12, 16, 16));

    });

    public SideBoardBlock(Properties settings) {
        super(settings, BlockEntityRegistry.SIDEBOARD::get);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SideBoardBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return null;
    }
    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("block.brewery.expandable.tooltip").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
}
