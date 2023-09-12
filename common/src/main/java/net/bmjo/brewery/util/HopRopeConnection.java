package net.bmjo.brewery.util;

import dev.architectury.networking.NetworkManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.block.HangingRope;
import net.bmjo.brewery.client.RopeHelper;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.entity.RopeCollisionEntity;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.EntityRegister;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class HopRopeConnection {
    public static final double VISIBLE_RANGE = 2048.0D; //TODO
    private static final float COLLIDER_SPACING = 1.5f;
    private final HopRopeKnotEntity from;
    private final Entity to;
    private boolean alive = true;
    private final IntList collisionStorage = new IntArrayList(16);
    public boolean removeSilently = false;

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
            connection.createCollision();
            //createHangingRopes(fromKnot.level, connection);
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
        if (!collisionStorage.isEmpty()) return;
        if (from.getLevel().isClientSide()) return;

        double distance = from.distanceTo(to);
        // step = spacing * √(width^2 + width^2) / distance
        double step = COLLIDER_SPACING * Math.sqrt(Math.pow(EntityRegister.ROPE_COLLISION.get().getWidth(), 2) * 2) / distance;
        double v = step;
        // reserve space for the center collider
        double centerHoldout = EntityRegister.ROPE_COLLISION.get().getWidth() / distance;

        while (v < 0.5 - centerHoldout) {
            Entity collider1 = spawnCollision(false, from, to, v);
            if (collider1 != null) collisionStorage.add(collider1.getId());
            Entity collider2 = spawnCollision(true, from, to, v);
            if (collider2 != null) collisionStorage.add(collider2.getId());

            v += step;
        }

        Entity centerCollider = spawnCollision(false, from, to, 0.5);
        if (centerCollider != null) collisionStorage.add(centerCollider.getId());
    }

    @Nullable
    private Entity spawnCollision(boolean reverse, Entity start, Entity end, double v) {
        assert from.getLevel() instanceof ServerLevel;
        Vec3 startPos = start.position().add(start.getLeashOffset());
        Vec3 endPos = end.position().add(end.getLeashOffset());

        Vec3 tmp = endPos;
        if (reverse) {
            endPos = startPos;
            startPos = tmp;
        }


        Vec3 offset = RopeHelper.getChainOffset(startPos, endPos);
        startPos = startPos.add(offset.x(), 0, offset.z());
        endPos = endPos.add(-offset.x(), 0, -offset.z());

        double distance = startPos.distanceTo(endPos);

        double x = Mth.lerp(v, startPos.x(), endPos.x());
        double y = startPos.y() + RopeHelper.drip2((v * distance), distance, endPos.y() - startPos.y());
        double z = Mth.lerp(v, startPos.z(), endPos.z());

        y -= EntityRegister.ROPE_COLLISION.get().getHeight() + 2 / 16f;

        RopeCollisionEntity collisionEntity = RopeCollisionEntity.create(from.getLevel(), x, y, z, this);
        if (from.getLevel().addFreshEntity(collisionEntity)) {
            return collisionEntity;
        } else {
            Brewery.LOGGER.warn("Tried to summon collision entity for a chain, failed to do so");
            return null;
        }
    }

    private static void createHangingRopes(Level level, HopRopeConnection connection) {
        List<BlockPos> crossingBlocks = BreweryMath.lineIntersection(connection);
        for (BlockPos blockPos : crossingBlocks) {
            if (level.getBlockState(blockPos).isAir()) {
                level.setBlock(blockPos, ObjectRegistry.HANGING_ROPE.get().defaultBlockState().setValue(HangingRope.TOP, true), 3);
            }
        }
    }

    public boolean needsBeDestroyed() {
        return from.isRemoved() || to.isRemoved();
    }

    public void destroy(boolean mayDrop) {
        System.out.println("destroy");
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
        for (Integer entityId : collisionStorage) {
            Entity e = from.getLevel().getEntity(entityId);
            if (e instanceof RopeCollisionEntity) {
                e.remove(Entity.RemovalReason.DISCARDED);
            } else {
                Brewery.LOGGER.warn("Collision storage contained reference to {} (#{}) which is not a collision entity.", e, entityId);
            }
        }
        collisionStorage.clear();
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
