package net.bmjo.brewery.block.brewingstation;

import net.bmjo.brewery.block.property.BrewMaterial;
import net.bmjo.brewery.block.property.Heat;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BrewOvenBlock extends BrewingstationBlock {
    public static final EnumProperty<Heat> HEAT;
    private static final Supplier<VoxelShape> voxelShapeSupplier;
    public static final Map<Direction, VoxelShape> SHAPE;

    public BrewOvenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(MATERIAL, BrewMaterial.WOOD).setValue(HEAT, Heat.OFF));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (blockState.getValue(HEAT) != Heat.LIT && AbstractFurnaceBlockEntity.getFuel().containsKey(itemStack.getItem())) {
            level.setBlock(blockPos, blockState.setValue(HEAT, Heat.LIT), 3);
            level.playSound(null, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(HEAT) != Heat.OFF) {
            double x = blockPos.getX() + 0.5D;
            double y = blockPos.getY();
            double z = blockPos.getZ() + 0.5D;
            if (randomSource.nextDouble() < 0.1D) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = blockState.getValue(FACING).getCounterClockWise();
            Direction.Axis axis = direction.getAxis();
            double h = randomSource.nextDouble() * 0.6D - 0.3D;
            double i = axis == Direction.Axis.X ? direction.getStepX() * 0.52D : h;
            double j = randomSource.nextDouble() * 10.0D / 16.0D;
            double k = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52D : h;
            level.addParticle(ParticleTypes.SMOKE, x + i, y + j, z + k, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x + i, y + j, z + k, 0.0, 0.0, 0.0);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MATERIAL ,HEAT);
    }

    static {
        HEAT = BlockStateRegistry.HEAT;
        voxelShapeSupplier = () -> {
            VoxelShape shape = Shapes.empty();
            shape = Shapes.or(shape, Shapes.box(0.125, 0, 0, 1, 0.125, 0.875));
            shape = Shapes.or(shape, Shapes.box(0, 0.125, 0, 1, 1, 0.9375));
            return shape;
        };
        SHAPE = Util.make(new HashMap<>(), map -> {
            for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
                map.put(direction, BreweryUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
            }
        });
    }
}
