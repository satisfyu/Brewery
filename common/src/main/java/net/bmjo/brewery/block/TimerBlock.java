package net.bmjo.brewery.block;

import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TimerBlock extends BrewKettleBlock {
    public static final BooleanProperty TIME;

    public TimerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TIME, false));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!blockState.getValue(COMPLETE)) return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
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
}
