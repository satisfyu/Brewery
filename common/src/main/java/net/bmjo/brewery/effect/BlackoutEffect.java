package net.bmjo.brewery.effect;


import net.bmjo.brewery.alcohol.AlcoholPlayer;
import net.bmjo.brewery.alcohol.AlcoholManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class BlackoutEffect extends MobEffect {
    public BlackoutEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x111111);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        MobEffectInstance effect = livingEntity.getEffect(BreweryEffects.BLACKOUT.get());
        assert effect != null;
        int duration = effect.getDuration();
        switch (duration) {
            case AlcoholManager.FALL_DOWN -> {
                Level level = livingEntity.getLevel();
                BlockState blockState = livingEntity.getBlockStateOn();
                SoundEvent soundEvent = blockState.getBlock().getSoundType(blockState).getFallSound();
                livingEntity.playSound(soundEvent, 1.0f, 1.0f);
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            case AlcoholManager.WANDER_AROUND -> AlcoholManager.movePlayer(livingEntity, livingEntity.getLevel());
        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            alcoholPlayer.getAlcohol().soberUp();
            if (livingEntity.hasEffect(BreweryEffects.DRUNK.get())) {
                livingEntity.removeEffect(BreweryEffects.DRUNK.get());
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.getAlcohol());
            }
        }
        super.removeAttributeModifiers(livingEntity, attributeMap, i);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == AlcoholManager.FALL_DOWN || duration == AlcoholManager.WANDER_AROUND;
    }
}