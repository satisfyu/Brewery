package net.bmjo.brewery.block;

import net.bmjo.brewery.block.entity.BrewKettleEntity;
import net.bmjo.brewery.block.property.BlockStateRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class BrewKettleBlock extends Block {
    public static final BooleanProperty COMPLETE;

    protected BrewKettleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(COMPLETE, false));
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            checkComplete(level, blockPos);
        }
        super.onPlace(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            BlockPos controllerPos = getControllerPos(level, blockPos);
            if (level.getBlockEntity(blockPos) instanceof BrewKettleEntity brewKettleEntity) {
                brewKettleEntity.deactivate();
            }
            else if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof BrewKettleEntity brewKettleEntity) {
                brewKettleEntity.deactivate();
            }
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    private void checkComplete(Level level, BlockPos centerPos) {
        for (int exponent = 0; exponent < 4; exponent++) {
            int xOffset = BreweryUtil.power(-1, (exponent + 1) / 2);
            int yOffset = BreweryUtil.power(-1, exponent % 2);
            Set<BlockPos> blockPos = Sets.newHashSet(centerPos, centerPos.offset(xOffset, 0 , 0), centerPos.offset(0, 0 , yOffset), centerPos.offset(xOffset, 0 , yOffset));
            if (hasComponents(level, blockPos)) {
                BlockPos controllerPos = getControllerPos(blockPos, level);
                if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof BrewKettleEntity brewKettleEntity) {
                    brewKettleEntity.activate(blockPos);
                }
                return;
            }
        }
    }

    private boolean hasComponents(Level level, Set<BlockPos> blockPos) {
        final boolean[] components = {false, false, false, false};
        Stream<BlockState> blockStates = blockPos.stream().map(level::getBlockState);
        return blockStates.allMatch(blockState -> {
            if (!components[0] && blockState.getBlock() instanceof WaterBasin && !blockState.getValue(COMPLETE)) {
                components[0] = true;
                return true;
            }
            if (!components[1] && blockState.getBlock() instanceof SteamWhistle && !blockState.getValue(COMPLETE)) {
                components[1] = true;
                return true;
            }
            if (!components[2] && blockState.getBlock() instanceof Oven && !blockState.getValue(COMPLETE)) {
                components[2] = true;
                return true;
            }
            if (!components[3] && blockState.getBlock() instanceof Timer && !blockState.getValue(COMPLETE)) {
                components[3] = true;
                return true;
            }
            return false;
        });
    }

    @Nullable
    private BlockPos getControllerPos(Set<BlockPos> blockPos, Level level) {
        return blockPos.stream().filter(pos -> level.getBlockState(pos).getBlock() instanceof WaterBasin).findFirst().orElse(null);
    }

    @Nullable
    public BlockPos getControllerPos(Level level, BlockPos centerPos) {
        Set<BlockPos> blockPos = new HashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                blockPos.add(centerPos.offset(x, 0, y));
            }
        }
        return blockPos.stream().filter(pos -> level.getBlockEntity(pos) instanceof BrewKettleEntity brewKettleEntity && brewKettleEntity.isPartOf(centerPos)).findAny().orElse(null);
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COMPLETE);
    }

    static {
        COMPLETE = BlockStateRegistry.COMPLETE;
    }
}
