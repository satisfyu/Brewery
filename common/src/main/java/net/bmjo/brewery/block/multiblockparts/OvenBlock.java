package net.bmjo.brewery.block.multiblockparts;

import net.bmjo.brewery.block.property.Heat;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class OvenBlock extends BrewKettleBlock {
    public static final EnumProperty<Heat> HEAT;
    public OvenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HEAT, Heat.OFF));
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!blockState.getValue(COMPLETE)) return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT);
    }

    static {
        HEAT = BlockStateRegistry.HEAT;
    }

}
