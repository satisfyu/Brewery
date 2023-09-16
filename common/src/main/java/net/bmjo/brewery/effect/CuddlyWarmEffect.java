package net.bmjo.brewery.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CuddlyWarmEffect extends InstantenousMobEffect {

    public CuddlyWarmEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF69B4);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.getLevel().isClientSide()){
            ServerLevel serverLevel = (ServerLevel) livingEntity.getLevel();
            livingEntity.setTicksFrozen(0);
            if (livingEntity instanceof SnowGolem && serverLevel.getServer().getTickCount() % 20 == 0) {
                livingEntity.hurt(DamageSource.MAGIC, amplifier + 1);
            }

            BlockPos pos = livingEntity.blockPosition();
            if (serverLevel.getBlockState(pos).getBlock() == Blocks.POWDER_SNOW) {
                Vec3 motion = livingEntity.getDeltaMovement();
                if (motion.y < 0) {
                    livingEntity.setDeltaMovement(motion.x, 0, motion.z);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public boolean isInstantenous() {
        return false;
    }
}