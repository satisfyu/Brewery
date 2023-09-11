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
    private final HopRopeKnotEntity first;
    private final Entity second;

    private boolean alive = true;

    private HopRopeConnection(HopRopeKnotEntity first, Entity second) {
        this.first = first;
        this.second = second;
    }

    @Nullable
    public static HopRopeConnection create(@NotNull HopRopeKnotEntity first, @NotNull Entity second) {
        HopRopeConnection connection = new HopRopeConnection(first, second);
        if (first.sameConnectionExist(connection)) return null;

        first.addConnection(connection);
        if (second instanceof HopRopeKnotEntity secondaryKnot) {
            secondaryKnot.addConnection(connection);
            //connection.createCollision();
        }
        if (!first.getLevel().isClientSide()) {
            connection.sendAttachChainPacket(first.getLevel());
        }
        return connection;
    }

    public static final double VISIBLE_RANGE = 2048.0D; //TODO

    private void sendAttachChainPacket(Level level) { //TODO weg
        assert level instanceof ServerLevel;

        Set<ServerPlayer> trackingPlayers = getTrackingPlayers(first.getLevel());
        FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();

        buf.writeInt(first.getId());
        buf.writeInt(second.getId());

        for (ServerPlayer player : trackingPlayers) {
            NetworkManager.sendToPlayer(player, BreweryNetworking.ATTACH_ROPE_S2C_ID, buf);
        }
    }

    private Set<ServerPlayer> getTrackingPlayers(Level level) {
        Set<ServerPlayer> trackingPlayers = new HashSet<>();
        if (level instanceof ServerLevel serverLevel) {
            trackingPlayers.addAll(serverLevel.players().stream().filter((player) -> player.distanceToSqr(first.position().x(), first.position().y(), first.position().z()) <= VISIBLE_RANGE).toList());
            trackingPlayers.addAll(serverLevel.players().stream().filter((player) -> player.distanceToSqr(second.position().x(), second.position().y(), second.position().z()) <= VISIBLE_RANGE).toList());
        }
        return trackingPlayers;
    }

    public HopRopeKnotEntity first() {
        return first;
    }

    public Entity second() {
        return second;
    }

    public boolean dead() {
        return !alive;
    }

    public double getSquaredDistance() {
        return this.first.distanceToSqr(second);
    }

    public boolean needsBeDestroyed() {
        return first.isRemoved() || second.isRemoved();
    }

    public void destroy() {
        if (!alive) return;
        this.alive = false;
        if (first.getLevel().isClientSide()) return;
        //destroyCollision();
        if (!first.isRemoved() && !second.isRemoved()) sendDetachChainPacket(first.getLevel());
    }

    private void sendDetachChainPacket(Level level) {
        assert level instanceof ServerLevel;

        List<ServerPlayer> trackingPlayers = ((ServerLevel) level).getChunkSource().chunkMap.getPlayers(new ChunkPos(new BlockPos(this.first().position())), false);
        FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();

        buf.writeInt(first.getId());
        buf.writeInt(second.getId());

        for (ServerPlayer player : trackingPlayers) {
            NetworkManager.sendToPlayer(player, BreweryNetworking.DETACH_ROPE_S2C_ID, buf);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HopRopeConnection that)) return false;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second) || Objects.equals(first, that.second) && Objects.equals(second, that.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
