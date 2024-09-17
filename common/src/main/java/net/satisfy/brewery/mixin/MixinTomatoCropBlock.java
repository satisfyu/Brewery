package net.satisfy.brewery.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.satisfy.brewery.block.entity.rope.HangingRopeEntity;
import net.satisfy.farm_and_charm.block.crops.TomatoCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TomatoCropBlock.class)
public abstract class MixinTomatoCropBlock {
    @Inject(method = "isRopeAbove", at = @At("HEAD"), cancellable = true)
    private static void modifyIsRopeAbove(LevelAccessor levelAccessor, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        AABB searchArea = new AABB(blockPos.above().getCenter(), blockPos.above().offset(1, HangingRopeEntity.MAX_LENGTH, 1).getCenter());
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
