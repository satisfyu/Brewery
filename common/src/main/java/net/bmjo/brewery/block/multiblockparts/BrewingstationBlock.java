package net.bmjo.brewery.block.multiblockparts;

import net.bmjo.brewery.block.property.BrewMaterial;
import net.bmjo.brewery.entity.BrewstationEntity;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class BrewingstationBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<BrewMaterial> MATERIAL;

    public BrewingstationBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(MATERIAL, BrewMaterial.WOOD));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BrewstationEntity brewstationEntity = getController(blockPos, level);
        if (brewstationEntity != null) {
            for (BlockPos pos : brewstationEntity.getComponents()) {
                if (!pos.equals(blockPos)) {
                    level.removeBlock(pos, false);
                }
            }
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Nullable
    protected BrewstationEntity getController(BlockPos centerPos, Level level) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                BlockEntity blockEntity = level.getBlockEntity(centerPos.offset(x, 0, y));
                if (blockEntity instanceof BrewstationEntity brewstationEntity && brewstationEntity.isPartOf(centerPos)) {
                    return brewstationEntity;
                }
            }
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, MATERIAL);
    }

    static {
        MATERIAL = BlockStateRegistry.MATERIAL;
    }

}
