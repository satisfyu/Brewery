package net.bmjo.brewery.mixin;

import com.google.common.collect.Maps;
import net.bmjo.brewery.registry.EffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Final
    @Shadow
    private final Map<MobEffect, MobEffectInstance> activeEffects = Maps.newHashMap();

    public boolean hasStatusEffect(MobEffect effect) {
        return activeEffects.containsKey(effect);
    }

    @Shadow @Nullable
    public abstract MobEffectInstance getEffect(MobEffect mobEffect);

    @ModifyVariable(method = "travel", at = @At("LOAD"), name = "f2", ordinal = 0, index = 8)
    public float inject2(float value) {
        if (this.hasStatusEffect(EffectRegistry.SLIDING.get()) && this.isOnGround()) {
            MobEffectInstance slidingEffect = this.getEffect(EffectRegistry.SLIDING.get());
            if (slidingEffect != null) {
                int amplifier = slidingEffect.getAmplifier();
                return (((amplifier/(-300f))+1)*0.98f);
            }
        }
        return value;
    }

    @Redirect(method = "calculateFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffect(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"))
    public MobEffectInstance improvedJumpBoostFall(LivingEntity livingEntity, MobEffect effect) {
        if (livingEntity.hasEffect(EffectRegistry.DOUBLEJUMP.get())) {
            return livingEntity.getEffect(EffectRegistry.DOUBLEJUMP.get());
        }
        return livingEntity.getEffect(MobEffects.JUMP);
    }

    @Inject(method = "getJumpBoostPower", at = @At(value = "HEAD"), cancellable = true)
    private void improvedJumpBoost(CallbackInfoReturnable<Double> cir) {
        if (this.hasStatusEffect(EffectRegistry.DOUBLEJUMP.get())) {
            MobEffectInstance doubleJumpEffect = this.activeEffects.get(EffectRegistry.DOUBLEJUMP.get());
            if (doubleJumpEffect != null) {
                cir.setReturnValue((double)(0.1F * (float)(doubleJumpEffect.getAmplifier() + 1)));
            }
        }
    }
}
