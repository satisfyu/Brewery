package net.satisfy.brewery.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CuddlyWarmEffect extends InstantenousMobEffect {

    public CuddlyWarmEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF69B4);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) livingEntity.getLevel();
            livingEntity.setTicksFrozen(0);
            if (livingEntity instanceof Stray && serverLevel.getServer().getTickCount() % 20 == 0) {
                livingEntity.hurt(DamageSource.MAGIC, amplifier + 1);
            }

            BlockPos pos = livingEntity.blockPosition();

            int radius = 2;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos targetPos = pos.offset(x, y, z);
                        if (serverLevel.getBlockState(targetPos).getBlock() == Blocks.POWDER_SNOW ||
                                serverLevel.getBlockState(targetPos).getBlock() == Blocks.SNOW) {
                            serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }

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
