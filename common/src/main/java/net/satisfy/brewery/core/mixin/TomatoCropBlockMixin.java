package net.satisfy.brewery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.satisfy.brewery.core.block.RopeBlock;
import net.satisfy.brewery.core.block.RopeKnotBlock;
import net.satisfy.farm_and_charm.core.block.crops.TomatoCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TomatoCropBlock.class)
public abstract class TomatoCropBlockMixin {
    @Inject(method = "isRopeAbove", at = @At("HEAD"), cancellable = true)
    private static void modifyIsRopeAbove(LevelAccessor level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.above()).getBlock() instanceof RopeBlock
                || level.getBlockState(pos.above()).getBlock() instanceof RopeKnotBlock) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
