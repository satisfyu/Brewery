package net.bmjo.brewery.effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class OmnivoreEffect extends MobEffect {
    public OmnivoreEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    //TODO
    }
}