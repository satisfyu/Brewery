package net.satisfy.brewery.core.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;

public class PacifyEffect extends MobEffect {
    public PacifyEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public MobEffect addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation, double d, AttributeModifier.Operation operation) {
        // TODO fixme
        /*
        if (entity instanceof Player) {
            AttributeInstance attribute = entity.getAttribute(Attributes.FOLLOW_RANGE);
            if (attribute != null) {
                attribute.setBaseValue(attribute.getValue() * 0.75);
            }
        }*/
        return super.addAttributeModifier(holder, resourceLocation, d, operation);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        // TODO fixme
        /*
        if (entity instanceof Player) {
            AttributeInstance attribute = entity.getAttribute(Attributes.FOLLOW_RANGE);
            if (attribute != null) {
                attribute.setBaseValue(attribute.getValue() / 0.75);
            }
        }*/
        super.removeAttributeModifiers(attributeMap);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            entity.level().getEntitiesOfClass(EnderMan.class, entity.getBoundingBox().inflate(32.0D))
                    .forEach(enderman -> enderman.setTarget(null));
        }
        return super.applyEffectTick(entity, amplifier);
    }
}
