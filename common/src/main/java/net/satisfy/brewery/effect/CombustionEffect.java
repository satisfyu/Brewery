package net.satisfy.brewery.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

public class CombustionEffect extends MobEffect {
    public CombustionEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getCommandSenderWorld() instanceof ServerLevel) {
            entity.blockPosition();
            entity.level().getEntities(entity, entity.getBoundingBox().inflate(4)).forEach(e -> {
                if (e instanceof Monster && entity.getRandom().nextFloat() < 0.02) {
                    e.setRemainingFireTicks(100);
                }
            });
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}
