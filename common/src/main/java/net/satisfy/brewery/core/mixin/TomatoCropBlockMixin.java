package net.satisfy.brewery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.satisfy.brewery.core.block.entity.rope.HangingRopeEntity;
import net.satisfy.farm_and_charm.core.block.crops.TomatoCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TomatoCropBlock.class)
public abstract class TomatoCropBlockMixin {
    @Inject(method = "isRopeAbove", at = @At("HEAD"), cancellable = true)
    private static void modifyIsRopeAbove(LevelAccessor levelAccessor, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        AABB searchArea = new AABB(blockPos.above(), blockPos.above().offset(1, HangingRopeEntity.MAX_LENGTH, 1));
        List<HangingRopeEntity> results = levelAccessor.getEntitiesOfClass(HangingRopeEntity.class, searchArea);
        for (HangingRopeEntity hangingRope : results) {
            if (hangingRope.active()) {
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }
}
