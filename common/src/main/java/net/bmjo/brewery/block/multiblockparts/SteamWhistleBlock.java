package net.bmjo.brewery.block.multiblockparts;

import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SteamWhistleBlock extends BrewKettleBlock {
    public static final BooleanProperty WHISTLE;

    public SteamWhistleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WHISTLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WHISTLE);
    }

    static {
        WHISTLE = BlockStateRegistry.WHISTLE;
    }
}
