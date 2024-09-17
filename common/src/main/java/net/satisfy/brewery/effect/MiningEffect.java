package net.satisfy.brewery.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MiningEffect extends MobEffect {
    public MiningEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            int y = player.getBlockY();
            MobEffectInstance currentEffect = player.getEffect(MobEffects.DIG_SPEED);
            MobEffectInstance newEffect = determineEffectByYLevel(y);

            if (shouldUpdateEffect(currentEffect, newEffect)) {
                player.removeEffect(MobEffects.DIG_SPEED);
                player.addEffect(newEffect);
            }
        }
        return true;
    }

    private MobEffectInstance determineEffectByYLevel(int y) {
        if (y >= 50) {
            return new MobEffectInstance(MobEffects.DIG_SPEED, 200, 0, false, false);
        } else if (y >= 30) {
            return new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1, false, false);
        } else if (y >= 0) {
            return new MobEffectInstance(MobEffects.DIG_SPEED, 200, 2, false, false);
        } else if (y >= -20) {
            return new MobEffectInstance(MobEffects.DIG_SPEED, 200, 3, false, false);
        } else {
            return new MobEffectInstance(MobEffects.DIG_SPEED, 200, 4, false, false);
        }
    }

    private boolean shouldUpdateEffect(MobEffectInstance currentEffect, MobEffectInstance newEffect) {
        return currentEffect == null || currentEffect.getAmplifier() != newEffect.getAmplifier();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return i % 20 == 0;
    }
}
