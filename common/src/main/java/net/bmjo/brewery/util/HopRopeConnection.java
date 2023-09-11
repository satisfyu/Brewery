package net.bmjo.brewery.util;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class HopRopeConnection {


    public static final double VISIBLE_RANGE = 2048.0D; //TODO
    private final HopRopeKnotEntity from;
    private final Entity to;
    private boolean alive = true;

    public HopRopeKnotEntity from() {
        return from;
    }

    public Entity to() {
        return to;
    }

    public boolean dead() {
        return !alive;
    }

    public double getSquaredDistance() {
        return this.from.distanceToSqr(to);
    }

    private HopRopeConnection(HopRopeKnotEntity from, Entity to) {
        this.from = from;
        this.to = to;
    }

    @Nullable
    public static HopRopeConnection create(@NotNull HopRopeKnotEntity fromKnot, @NotNull Entity to) {
        HopRopeConnection connection = new HopRopeConnection(fromKnot, to);
        if (fromKnot.sameConnectionExist(connection)) return null;

        fromKnot.addConnection(connection);
        if (to instanceof HopRopeKnotEntity toKnot) {
            toKnot.addConnection(connection);
            //connection.createCollision();
        }
        if (fromKnot.getLevel() instanceof ServerLevel serverLevel) {
            Set<ServerPlayer> trackingPlayers = getTrackingPlayers(serverLevel, connection);

            FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
            buf.writeInt(fromKnot.getId());
            buf.writeInt(to.getId());

            for (ServerPlayer player : trackingPlayers) {
                NetworkManager.sendToPlayer(player, BreweryNetworking.ATTACH_ROPE_S2C_ID, buf);
            }
        }
        return connection;
    }

    public boolean needsBeDestroyed() {
        return from.isRemoved() || to.isRemoved();
    }

    public void destroy() {
        if (!alive) return;
        this.alive = false;
        if (from.getLevel().isClientSide()) return;
        //destroyCollision();
        if (from.getLevel() instanceof ServerLevel serverLevel && !from.isRemoved() && !to.isRemoved()) {
            Set<ServerPlayer> trackingPlayers = getTrackingPlayers(serverLevel, this);

            FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
            buf.writeInt(from.getId());
            buf.writeInt(to.getId());

            for (ServerPlayer player : trackingPlayers) {
                NetworkManager.sendToPlayer(player, BreweryNetworking.DETACH_ROPE_S2C_ID, buf);
            }
        }
    }

    private static Set<ServerPlayer> getTrackingPlayers(ServerLevel serverLevel, HopRopeConnection connection) {
        Set<ServerPlayer> trackingPlayers = new HashSet<>();
        HopRopeKnotEntity from = connection.from();
        Entity to = connection.to();
        trackingPlayers.addAll(serverLevel.players().stream().filter((player) -> player.distanceToSqr(from.position().x(), from.position().y(), from.position().z()) <= VISIBLE_RANGE).toList());
        trackingPlayers.addAll(serverLevel.players().stream().filter((player) -> player.distanceToSqr(to.position().x(), to.position().y(), to.position().z()) <= VISIBLE_RANGE).toList());
        return trackingPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HopRopeConnection that)) return false;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to) || Objects.equals(from, that.to) && Objects.equals(to, that.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
