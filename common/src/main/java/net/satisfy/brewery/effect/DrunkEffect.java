package net.satisfy.brewery.effect;


import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.networking.packet.DrunkEffectS2CPacket;
import net.satisfy.brewery.registry.MobEffectRegistry;

public class DrunkEffect extends MobEffect {
    public DrunkEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE0DD2F);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            if (alcoholLevel.isDrunk() && livingEntity.getRandom().nextFloat() < 0.5f) {
                alcoholLevel.gainImmunity();
            }
            alcoholLevel.sober();
            if (!alcoholLevel.isSober()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.DRUNK, AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
            }
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int i) {
        super.onEffectAdded(livingEntity, i);
        if(livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            if (alcoholLevel.isDrunk()) {
                setDrunkEffect(livingEntity, true);
            }
        }
    }

    public static void removeDrunk(LivingEntity livingEntity) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            if (!alcoholLevel.isDrunk()) {
                setDrunkEffect(livingEntity, false);
            }
        }
    }

    private int getDrunkAmplifier(LivingEntity livingEntity) {
        MobEffectInstance effect = livingEntity.getEffect(MobEffectRegistry.DRUNK);
        return effect != null ? effect.getAmplifier() : 0;
    }

    private static void setDrunkEffect(LivingEntity livingEntity, boolean activate) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            DrunkEffectS2CPacket packet = new DrunkEffectS2CPacket(activate);
            NetworkManager.sendToPlayer(serverPlayer, packet);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return i == 1;
    }
}