package net.satisfy.brewery.core.effect;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.satisfy.brewery.core.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.network.packet.DrunkEffectS2CPacket;
import net.satisfy.brewery.core.registry.MobEffectRegistry;

public class DrunkEffect extends MobEffect {
    public DrunkEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE0DD2F);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.brewery$getAlcohol();
            if (amplifier >= alcoholLevel.getImmunity() - 1) {
                setDrunkEffect(livingEntity, true);
            }
        }
    }

    @Override
    public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason removalReason) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.brewery$getAlcohol();
            if (!alcoholLevel.isDrunk()) {
                setDrunkEffect(livingEntity, false);
            }
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.brewery$getAlcohol();
            if (alcoholLevel.isDrunk() && livingEntity.getRandom().nextFloat() < 0.5f) {
                alcoholLevel.gainImmunity();
            }
            alcoholLevel.sober();
            if (!alcoholLevel.isSober()) {
                livingEntity.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.DRUNK.get()), AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
            }
        }
        return true;
    }

    private void setDrunkEffect(LivingEntity livingEntity, boolean activate) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new DrunkEffectS2CPacket(activate));
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == 1;
    }
}
