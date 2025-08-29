package net.satisfy.brewery.core.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.registry.MobEffectRegistry;

public class BlackoutEffect extends MobEffect {
    public BlackoutEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x111111);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        MobEffectInstance effect = livingEntity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()));
        if (effect == null) return true;
        int duration = effect.getDuration();
        switch (duration) {
            case AlcoholManager.FALL_DOWN -> {
                Level level = livingEntity.level();
                BlockState blockState = livingEntity.getBlockStateOn();
                SoundEvent soundEvent = blockState.getBlock().getSoundType(blockState).getFallSound();
                livingEntity.playSound(soundEvent, 1.0f, 1.0f);
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            case AlcoholManager.WANDER_AROUND -> AlcoholManager.movePlayer(livingEntity, livingEntity.level());
        }
        return true;
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        super.addAttributeModifiers(attributeMap, i);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);
    }

    @Override
    public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason removalReason) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            alcoholPlayer.brewery$getAlcohol().soberUp();
            if (livingEntity instanceof Player player) {
                if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.DRUNK.get()))) {
                    player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.DRUNK.get()));
                }
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == AlcoholManager.FALL_DOWN || duration == AlcoholManager.WANDER_AROUND;
    }
}
