package net.satisfy.brewery.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class RepulsionEffect extends MobEffect {
    public RepulsionEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.0), e -> e != player);
            for (LivingEntity target : entities) {
                double dx = target.getX() - player.getX();
                double dz = target.getZ() - player.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > 0) {
                    target.setDeltaMovement(target.getDeltaMovement().add((dx / distance) * (0.2 + 0.1 * amplifier), 0.1, (dz / distance) * (0.2 + 0.1 * amplifier)));
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return i % 40 == 0;
    }
}
