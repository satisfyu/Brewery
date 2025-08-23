package net.satisfy.brewery.core.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TemptGoal.class)
public class MobFollowPlayerMixin {
    @Inject(method = "shouldFollow", at = @At("HEAD"), cancellable = true)
    private void followEffectCheck(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Player player) {
            if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.SNOWWHITE.get()))) {
                cir.setReturnValue(true);
            }
        }
    }
}