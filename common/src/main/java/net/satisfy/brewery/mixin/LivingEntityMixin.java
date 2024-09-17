package net.satisfy.brewery.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.satisfy.brewery.effect.BlackoutEffect;
import net.satisfy.brewery.effect.DrunkEffect;
import net.satisfy.brewery.effect.HaleyEffect;
import net.satisfy.brewery.effect.PacifyEffect;
import net.satisfy.brewery.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "removeEffect", at = @At("HEAD"))
    public void brewery$removeEffect(Holder<MobEffect> holder, CallbackInfoReturnable<Boolean> cir) {
        if (holder == MobEffectRegistry.BLACKOUT) {
            BlackoutEffect.onRemove((LivingEntity) (Object) this);
        } else if (holder == MobEffectRegistry.DRUNK) {
            DrunkEffect.removeDrunk((LivingEntity) (Object) this);
        } else if (holder == MobEffectRegistry.HALEY) {
            HaleyEffect.onRemove((LivingEntity) (Object) this);
        } else if(holder == MobEffectRegistry.PACIFY){
            PacifyEffect.onRemove((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    public void brewery$onEffectRemoved(MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        if (mobEffectInstance.getEffect() == MobEffectRegistry.BLACKOUT) {
            BlackoutEffect.onRemove((LivingEntity) (Object) this);
        } else if (mobEffectInstance.getEffect() == MobEffectRegistry.DRUNK) {
            DrunkEffect.removeDrunk((LivingEntity) (Object) this);
        } else if (mobEffectInstance.getEffect() == MobEffectRegistry.HALEY) {
            HaleyEffect.onRemove((LivingEntity) (Object) this);
        } else if(mobEffectInstance.getEffect() == MobEffectRegistry.PACIFY){
            PacifyEffect.onRemove((LivingEntity) (Object) this);
        }
    }
}
