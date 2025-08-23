package net.satisfy.brewery.core.effect;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.registry.MobEffectRegistry;

public class BlackoutEffect extends MobEffect {
    public BlackoutEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x111111);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        MobEffectInstance effect = livingEntity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()));
        assert effect != null;
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
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        // TODO fixme
        /*
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            alcoholPlayer.brewery$getAlcohol().soberUp();
            if (livingEntity.hasEffect(MobEffectRegistry.DRUNK.get())) {
                livingEntity.removeEffect(MobEffectRegistry.DRUNK.get());
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.brewery$getAlcohol());
            }
        }*/
        super.removeAttributeModifiers(attributeMap);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == AlcoholManager.FALL_DOWN || duration == AlcoholManager.WANDER_AROUND;
    }
}