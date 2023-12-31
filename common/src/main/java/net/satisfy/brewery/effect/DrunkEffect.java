package net.satisfy.brewery.effect;


import dev.architectury.networking.NetworkManager;
import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.networking.BreweryNetworking;
import net.satisfy.brewery.registry.EffectRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class DrunkEffect extends MobEffect {
    public DrunkEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE0DD2F);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            if (alcoholLevel.isDrunk() && livingEntity.getRandom().nextFloat() < 0.5f) {
                alcoholLevel.gainImmunity();
            }
            alcoholLevel.sober();
            if (!alcoholLevel.isSober()) {
                livingEntity.addEffect(new MobEffectInstance(EffectRegistry.DRUNK.get(), AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            int amplifier = getDrunkAmplifier(livingEntity);
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            if (amplifier >= alcoholLevel.getImmunity() - 1) {
                setDrunkEffect(livingEntity, true);
            }
        }
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            if (!alcoholLevel.isDrunk()) {
                setDrunkEffect(livingEntity, false);
            }
        }
        super.removeAttributeModifiers(livingEntity, attributeMap, i);
    }

    private int getDrunkAmplifier(LivingEntity livingEntity) {
        MobEffectInstance effect = livingEntity.getEffect(EffectRegistry.DRUNK.get());
        return effect != null ? effect.getAmplifier() : 0;
    }

    private void setDrunkEffect(LivingEntity livingEntity, boolean activate) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
            buf.writeBoolean(activate);
            NetworkManager.sendToPlayer(serverPlayer, BreweryNetworking.DRUNK_EFFECT_S2C_ID, buf);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 1;
    }

}