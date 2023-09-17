package net.bmjo.brewery.effect;

import net.bmjo.brewery.effect.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.Vec3;

public class SlidingEffect extends InstantenousMobEffect {
    public SlidingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF69B4);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.getLevel().isClientSide()) {
            if (livingEntity instanceof ServerPlayer serverPlayer && !livingEntity.isSpectator()) {
                Vec3 pos;
                if (serverPlayer.getRespawnPosition() != null && (serverPlayer.getLevel().getBlockState(serverPlayer.getRespawnPosition()).getBlock() instanceof BedBlock)) {
                    pos = Vec3.atBottomCenterOf(serverPlayer.getRespawnPosition());
                    serverPlayer.connection.teleport(pos.x, pos.y, pos.z, Mth.wrapDegrees(serverPlayer.getYRot()), Mth.wrapDegrees(serverPlayer.getXRot()));
                } else {
                    pos = Vec3.atBottomCenterOf(serverPlayer.getLevel().getSharedSpawnPos());
                    serverPlayer.connection.teleport(pos.x, pos.y, pos.z, Mth.wrapDegrees(serverPlayer.getYRot()), Mth.wrapDegrees(serverPlayer.getXRot()));
                }
                //TODO funktioniert das nicht weils falsch gemacht ist oder overrided der slide effekt den movement speed?
                float newSpeed = (float) (serverPlayer.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).getBaseValue() * 1.4);
                serverPlayer.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);
            } else {
                Vec3 pos = Vec3.atBottomCenterOf(livingEntity.getLevel().getSharedSpawnPos());
                livingEntity.teleportTo(pos.x, pos.y, pos.z);
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
