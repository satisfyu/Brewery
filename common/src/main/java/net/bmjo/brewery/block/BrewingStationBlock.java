package net.bmjo.brewery.block;

import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.List;


public class BrewingStationBlock extends Block {

    public static final EnumProperty<BreweryUtil.LocationState> LOCATION = EnumProperty.create("location", BreweryUtil.LocationState.class);

    public BrewingStationBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(LOCATION, BreweryUtil.LocationState.CENTER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOCATION);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!level.isClientSide) {
            BlockPos mainPos = pos;
            BlockState mainState = state;
            Direction facing = placer.getDirection();

            switch (facing) {
                case NORTH:
                    mainPos = pos.south();
                    mainState = defaultBlockState().setValue(LOCATION, BreweryUtil.LocationState.TOP_LEFT);
                    break;
                case SOUTH:
                    mainPos = pos.north();
                    mainState = defaultBlockState().setValue(LOCATION, BreweryUtil.LocationState.BOTTOM_RIGHT);
                    break;
                case EAST:
                    mainPos = pos.west();
                    mainState = defaultBlockState().setValue(LOCATION, BreweryUtil.LocationState.BOTTOM_LEFT);
                    break;
                case WEST:
                    mainPos = pos.east();
                    mainState = defaultBlockState().setValue(LOCATION, BreweryUtil.LocationState.TOP_RIGHT);
                    break;
            }
            level.setBlock(mainPos, mainState, 3);
        }
    }
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        breakPad(level, level.getBlockState(pos), pos, !player.isCreative());
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos offset = pos.relative(direction);
                BlockState state = level.getBlockState(offset);
                if (state.getBlock().equals(this)) {
                    breakPad(level, state, offset, true);
                    break;
                }
            }
        }
        super.wasExploded(level, pos, explosion);
    }

    public void breakPad(Level level, BlockState state, BlockPos pos, boolean drop) {
        if (!level.isClientSide && state.getBlock().equals(this)) {
            BlockPos mainPos = this.getMainPos(state, pos);
            getBlockPosAround(mainPos).forEach(blockPos -> level.destroyBlock(blockPos, drop));
            level.destroyBlock(mainPos, drop);
        }
    }

    public List<BlockPos> getBlockPosAround(BlockPos pos) {
        return List.of(pos, pos.north(), pos.east(), pos.south(), pos.west(), pos.north().east(), pos.north().west(), pos.south().east(), pos.south().west());
    }

    public BlockPos getMainPos(BlockState state, BlockPos from) {
        BreweryUtil.LocationState location = state.getValue(LOCATION);
        switch (location) {
            case TOP_LEFT:
                return from.south().east();
            case TOP:
                return from.south();
            case BOTTOM:
                return from.east();
            default:
                return from;
        }
    }

}