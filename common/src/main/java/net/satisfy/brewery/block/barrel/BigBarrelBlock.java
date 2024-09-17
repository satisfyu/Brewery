package net.satisfy.brewery.block.barrel;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.satisfy.brewery.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class BigBarrelBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<BigBarrelBlock> CODEC = simpleCodec(BigBarrelBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF;

    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    }

    public BigBarrelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        if (!(this instanceof BigBarrelMainBlock)) {
            return ObjectRegistry.BARREL_MAIN.get().getCloneItemStack(levelReader, blockPos, blockState);
        }
        return super.getCloneItemStack(levelReader, blockPos, blockState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
