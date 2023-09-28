package net.bmjo.brewery.mixin;

import net.bmjo.brewery.item.CornSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(StonecutterBlock.class)
public abstract class StoneCutterMixin extends Block {
    public StoneCutterMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        CornSeedItem.handleStoneCutter(level, pos, entity);
    }
}
