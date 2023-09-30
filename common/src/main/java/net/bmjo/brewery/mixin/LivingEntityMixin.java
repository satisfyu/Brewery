package net.bmjo.brewery.mixin;

import com.google.common.collect.Maps;
import net.bmjo.brewery.registry.EffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
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
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow
    @Final
    private final Map<MobEffect, MobEffectInstance> activeEffects = Maps.newHashMap();

    @Unique
    public boolean hasStatusEffect(MobEffect effect) {
        return activeEffects.containsKey(effect);
    }

    @Shadow @Nullable
    public abstract MobEffectInstance getEffect(MobEffect mobEffect);

    @Shadow
    public abstract boolean removeAllEffects();

    @Shadow
    public abstract boolean addEffect(MobEffectInstance mobEffectInstance);

    @Shadow
    public void setHealth(float v) {}

    @Shadow
    public abstract boolean hasEffect(MobEffect mobEffect);

    @Shadow public abstract boolean removeEffect(MobEffect mobEffect);

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    public void inject1(DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        if (this.hasEffect(EffectRegistry.SURVIVALIST.get())) {
            this.setHealth(1.0F);
            this.removeEffect(EffectRegistry.SURVIVALIST.get());
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            this.level.broadcastEntityEvent(this, (byte) 35);
            callback.setReturnValue(true);
        }
    }

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
}
