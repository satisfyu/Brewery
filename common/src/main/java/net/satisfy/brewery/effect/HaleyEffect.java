package net.satisfy.brewery.effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HaleyEffect extends MobEffect {
    public HaleyEffect(MobEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (!player.getCommandSenderWorld().isClientSide) {
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }
        return true;
    }

    public static void onRemove(LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!player.getCommandSenderWorld().isClientSide) {
                player.getAbilities().mayfly = player.isCreative();
                player.getAbilities().flying = player.isCreative();
                player.onUpdateAbilities();
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}