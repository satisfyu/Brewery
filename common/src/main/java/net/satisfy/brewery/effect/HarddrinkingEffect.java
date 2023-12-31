package net.satisfy.brewery.effect;

import net.satisfy.brewery.registry.EffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HarddrinkingEffect extends MobEffect {
    public HarddrinkingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF00);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.hasEffect(EffectRegistry.DRUNK.get())) {
            livingEntity.removeEffect(EffectRegistry.DRUNK.get());
        }
        super.applyEffectTick(livingEntity, amplifier);
    }
}
