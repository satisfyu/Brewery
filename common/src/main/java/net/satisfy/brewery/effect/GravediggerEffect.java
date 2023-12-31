package net.satisfy.brewery.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class GravediggerEffect extends InstantenousMobEffect {

    public GravediggerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF69B4);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.getLevel().isClientSide()){
            if (livingEntity instanceof ServerPlayer serverPlayer && !livingEntity.isSpectator()) {
                if (serverPlayer.getLastDeathLocation().isPresent()) {
                    if (serverPlayer.getLevel().dimension() == serverPlayer.getLastDeathLocation().get().dimension()) {
                        Vec3 pos = Vec3.atBottomCenterOf(serverPlayer.getLastDeathLocation().get().pos());
                        serverPlayer.connection.teleport(pos.x, pos.y, pos.z, Mth.wrapDegrees(serverPlayer.getYRot()), Mth.wrapDegrees(serverPlayer.getXRot()));
                    }
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 1;
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }
}