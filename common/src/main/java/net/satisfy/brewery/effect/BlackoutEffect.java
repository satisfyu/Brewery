package net.satisfy.brewery.effect;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.registry.MobEffectRegistry;

public class BlackoutEffect extends MobEffect {
    public BlackoutEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x111111);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        MobEffectInstance effect = livingEntity.getEffect(MobEffectRegistry.BLACKOUT);
        assert effect != null;
        int duration = effect.getDuration();
        switch (duration) {
            case AlcoholManager.FALL_DOWN -> {
                Level level = livingEntity.level();
                BlockState blockState = livingEntity.getBlockStateOn();
                SoundEvent soundEvent = blockState.getSoundType().getFallSound();
                livingEntity.playSound(soundEvent, 1.0f, 1.0f);
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            case AlcoholManager.WANDER_AROUND -> AlcoholManager.movePlayer(livingEntity, livingEntity.level());
        }
        return true;
    }

    public static void onRemove(LivingEntity livingEntity) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            alcoholPlayer.getAlcohol().soberUp();
            if (livingEntity.hasEffect(MobEffectRegistry.DRUNK)) {
                livingEntity.removeEffect(MobEffectRegistry.DRUNK);
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.getAlcohol());
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return i == AlcoholManager.FALL_DOWN || i == AlcoholManager.WANDER_AROUND;
    }
}