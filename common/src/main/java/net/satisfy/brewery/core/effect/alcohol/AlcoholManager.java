package net.satisfy.brewery.core.effect.alcohol;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.satisfy.brewery.core.network.BreweryNetworking;
import net.satisfy.brewery.core.network.packet.AlcoholSyncS2CPacket;
import net.satisfy.brewery.core.registry.MobEffectRegistry;

public class AlcoholManager {
    public static final int BEGIN_TIME = 10 * 20;
    public static final int WANDER_AROUND = 5 * 20;
    public static final int FALL_DOWN = 7 * 20;
    public static final int DRUNK_TIME = 30 * 20;

    public static void drinkAlcohol(ServerPlayer serverPlayer) {
        if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.brewery$getAlcohol();
            alcoholLevel.drink();

            serverPlayer.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.DRUNK.get()), AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
            if (alcoholLevel.isBlackout()) {
                if (!serverPlayer.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()))) {
                    serverPlayer.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()), 15 * 20, 0, false, false));
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 13 * 20, 0, false, false));
                }
            }

            AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
        }
    }

    public static void syncAlcohol(ServerPlayer serverPlayer, AlcoholLevel alcoholLevel) {
        NetworkManager.sendToPlayer(serverPlayer, new AlcoholSyncS2CPacket(alcoholLevel.getDrunkenness(), alcoholLevel.getImmunity()));
    }

    public static void movePlayer(LivingEntity livingEntity, Level level) {
        if (!level.isClientSide) {
            for (int i = 0; i < 16; ++i) {
                double g = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                double h = Mth.clamp(livingEntity.getY() + (livingEntity.getRandom().nextInt(16) - 8), level.getMinBuildHeight(), level.getMinBuildHeight() + ((ServerLevel) level).getLogicalHeight() - 1);
                double j = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                if (livingEntity.isPassenger()) {
                    livingEntity.stopRiding();
                }

                Vec3 vec3 = livingEntity.position();
                if (livingEntity.randomTeleport(g, h, j, true)) {
                    level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(livingEntity));
                }
            }
        }
    }

    public static Vec3 stagger(Vec3 deltaMovement, RandomSource random) {
        double x = deltaMovement.x == 0 && deltaMovement.z != 0 ? 0.1 : deltaMovement.x;
        x *= ((double) random.nextInt(2 + 1) - 1) / 4.0;
        double z = deltaMovement.z == 0 && deltaMovement.x != 0 ? 0.1 : deltaMovement.z;
        z *= ((double) random.nextInt(2 + 1) - 1) / 4.0;
        return deltaMovement.add(x, 0, z);
    }
}
