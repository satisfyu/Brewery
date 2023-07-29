package net.bmjo.brewery.block;

import net.bmjo.brewery.block.property.BlockStateRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SteamWhistle extends BrewKettleBlock {
    public static final BooleanProperty WHISTLE;

    public SteamWhistle(Properties properties) {
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