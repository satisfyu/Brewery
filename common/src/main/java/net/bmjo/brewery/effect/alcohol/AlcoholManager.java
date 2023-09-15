package net.bmjo.brewery.effect.alcohol;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class AlcoholManager {
    public static final int BEGIN_TIME = 10 * 20;
    public static final int WANDER_AROUND = 5 * 20;
    public static final int FALL_DOWN = 7 * 20;
    public static final int DRUNK_TIME = 30 * 20;

    public static void syncAlcohol(ServerPlayer serverPlayer, AlcoholLevel alcoholLevel) {
        FriendlyByteBuf buffer = BreweryNetworking.createPacketBuf();
        buffer.writeInt(alcoholLevel.getDrunkenness());
        buffer.writeInt(alcoholLevel.getImmunity());
        NetworkManager.sendToPlayer(serverPlayer, BreweryNetworking.ALCOHOL_SYNC_S2C_ID, buffer);
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
        x *= ((double)random.nextInt(2 + 1) - 1) / 4.0;
        double z = deltaMovement.z == 0 && deltaMovement.x != 0 ? 0.1 : deltaMovement.z;
        z *= ((double)random.nextInt(2 + 1) - 1) / 4.0;
        return deltaMovement.add(x, 0, z);
    }
}
