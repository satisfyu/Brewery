package net.bmjo.brewery.util.rope;

import dev.architectury.networking.NetworkManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.entity.RopeCollisionEntity;
import net.bmjo.brewery.entity.RopeKnotEntity;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.registry.*;
import net.bmjo.brewery.util.BreweryMath;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RopeConnection {
    public static final double VISIBLE_RANGE = 2048.0D;
    private final RopeKnotEntity from;
    private final Entity to;
    private boolean alive = true;
    private final IntList collisions = new IntArrayList(32);
    public boolean removeSilently = false;

    public RopeKnotEntity from() {
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

    private RopeConnection(RopeKnotEntity from, Entity to) {
        this.from = from;
        this.to = to;
    }

    @Nullable
    public static RopeConnection create(@NotNull RopeKnotEntity fromKnot, @NotNull Entity to) {
        RopeConnection connection = new RopeConnection(fromKnot, to);
        if (fromKnot.sameConnectionExist(connection)) return null;

        fromKnot.addConnection(connection);
        if (to instanceof RopeKnotEntity toKnot) {
            toKnot.addConnection(connection);
            connection.createCollision();
            createHangingRopes(fromKnot.level, connection);
        }
        if (fromKnot.getLevel() instanceof ServerLevel serverLevel) {
            connection.sendAttachRopePacket(serverLevel);
        }
        return connection;
    }

    private void sendAttachRopePacket(ServerLevel serverLevel) {
        Set<ServerPlayer> trackingPlayers = getTrackingPlayers(serverLevel, this);

        FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
        buf.writeInt(from.getId());
        buf.writeInt(to.getId());

        for (ServerPlayer player : trackingPlayers) {
            NetworkManager.sendToPlayer(player, BreweryNetworking.ATTACH_ROPE_S2C_ID, buf);
        }
    }

    private void createCollision() {
        if (!collisions.isEmpty()) return;
        if (from.getLevel().isClientSide()) return;

        float distance = from.distanceTo(to);
        float step = (EntityRegistry.ROPE_COLLISION.get().getWidth() * 2.5F) / distance;
        float centerHoldout = EntityRegistry.ROPE_COLLISION.get().getWidth() / distance;

        for (float v = step; v < 0.5F - centerHoldout; v += step) {
            Entity fromCollider = spawnCollision(from, to, v);
            if (fromCollider != null) collisions.add(fromCollider.getId());
            Entity toCollider = spawnCollision(to, from, v);
            if (toCollider != null) collisions.add(toCollider.getId());
        }

        Entity centerCollider = spawnCollision(from, to, 0.5);
        if (centerCollider != null) collisions.add(centerCollider.getId());
    }

    @Nullable
    private Entity spawnCollision(Entity start, Entity end, double v) {
        assert from.getLevel() instanceof ServerLevel;
        Vec3 startPos = start.position().add(start.getLeashOffset());
        Vec3 endPos = end.position().add(end.getLeashOffset());

        double x = Mth.lerp(v, startPos.x(), endPos.x());
        double y = Mth.lerp(v, startPos.y(), endPos.y());
        double z = Mth.lerp(v, startPos.z(), endPos.z());
        y -= EntityRegistry.ROPE_COLLISION.get().getHeight() / 2;

        RopeCollisionEntity collisionEntity = RopeCollisionEntity.create(from.getLevel(), x, y, z, this);
        if (from.getLevel().addFreshEntity(collisionEntity)) {
            return collisionEntity;
        } else {
            Brewery.LOGGER.warn("FAILED to summon collision entity for a rope.");
            return null;
        }
    }

    private static void createHangingRopes(Level level, RopeConnection connection) {
        //TODO
    }

    public boolean needsBeDestroyed() {
        return from.isRemoved() || to.isRemoved();
    }

    public void destroy(boolean mayDrop) {
        if (!alive) return;
        this.alive = false;

        Level level = from.getLevel();
        if (level.isClientSide()) return;

        boolean drop = mayDrop;
        if (to instanceof Player player && player.isCreative()) drop = false;
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) drop = false;

        if (drop) {
            ItemStack stack = new ItemStack(ObjectRegistry.HOP_ROPE.get());
            if (to instanceof Player player) {
                player.addItem(stack);
            } else {
                Vec3 middle = BreweryMath.middleOf(from.position(), to.position());
                ItemEntity itemEntity = new ItemEntity(level, middle.x, middle.y, middle.z, stack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        destroyCollision();
        if (from.getLevel() instanceof ServerLevel serverLevel && !from.isRemoved() && !to.isRemoved()) {
            sendDetachChainPacket(serverLevel);
        }
    }

    private void sendDetachChainPacket(ServerLevel serverLevel) {
        Set<ServerPlayer> trackingPlayers = getTrackingPlayers(serverLevel, this);

        FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
        buf.writeInt(from.getId());
        buf.writeInt(to.getId());

        for (ServerPlayer player : trackingPlayers) {
            NetworkManager.sendToPlayer(player, BreweryNetworking.DETACH_ROPE_S2C_ID, buf);
        }
    }

    private void destroyCollision() {
        for (Integer entityId : collisions) {
            Entity e = from.getLevel().getEntity(entityId);
            if (e instanceof RopeCollisionEntity) {
                e.remove(Entity.RemovalReason.DISCARDED);
            } else {
                Brewery.LOGGER.warn("Collision storage contained reference to {} (#{}) which is not a collision entity.", e, entityId);
            }
        }
        collisions.clear();
    }

    private static Set<ServerPlayer> getTrackingPlayers(ServerLevel serverLevel, RopeConnection connection) {
        Set<ServerPlayer> trackingPlayers = new HashSet<>();
        RopeKnotEntity from = connection.from();
        Entity to = connection.to();
        trackingPlayers.addAll(serverLevel.players().stream().filter((player) -> player.distanceToSqr(from.position().x(), from.position().y(), from.position().z()) <= VISIBLE_RANGE).toList());
        trackingPlayers.addAll(serverLevel.players().stream().filter((player) -> player.distanceToSqr(to.position().x(), to.position().y(), to.position().z()) <= VISIBLE_RANGE).toList());
        return trackingPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RopeConnection that)) return false;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to) || Objects.equals(from, that.to) && Objects.equals(to, that.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
