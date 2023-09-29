package net.bmjo.brewery.entity;

import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.util.rope.RopeConnection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HangingRopeEntity extends Entity implements IRopeEntity {
    private static final int MAX_LENGTH = 8;
    @Nullable
    private RopeConnection connection;
    @Environment(EnvType.CLIENT)
    private int length, checkTimer = 0;

    public HangingRopeEntity(EntityType<? extends HangingRopeEntity> entityType, Level level) {
        super(entityType, level);
    }

    private HangingRopeEntity(Level level, double x, double y, double z, @NotNull RopeConnection connection) {
        this(EntityRegistry.HANGING_ROPE.get(), level);
        this.connection = connection;
        this.setPos(x, y, z);
    }

    public static HangingRopeEntity create(Level level, double x, double y, double z, RopeConnection connection) {
        return new HangingRopeEntity(level, x, y, z, connection);
    }

    @Nullable
    public static HangingRopeEntity getHangingRopeEntity(Level level, BlockPos pos) {
        List<HangingRopeEntity> results = level.getEntitiesOfClass(HangingRopeEntity.class, AABB.ofSize(Vec3.atLowerCornerOf(pos), 2, 2, 2));

        for (HangingRopeEntity current : results) {
            if (new BlockPos(current.position()).equals(pos)) {
                return current;
            }
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public Vec3 getRopeVec() {
        return new Vec3(0, -length, 0);
    }

    @Override
    public void tick() {
        if (getLevel().isClientSide()) {
            if (checkTimer++ == 50) {
                checkTimer = 0;
                checkLength();
            }
            return;
        }
        if (connection != null && connection.needsBeDestroyed()) connection.destroy(true);

        if (connection == null || connection.dead()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Environment(EnvType.CLIENT)
    private void checkLength() {
        BlockPos blockPos = this.blockPosition();
        int length = 0;
        while (this.level.getBlockState(blockPos.below(length + 1)).isAir() && length < MAX_LENGTH) {
            length++;
        }
        this.length = length;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) { //TODO make removable
        if (IRopeEntity.canDestroyWith(player.getItemInHand(interactionHand))) {
            destroyConnections(!player.isCreative());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player player) {
            hurt(DamageSource.playerAttack(player), 0.0F);
        } else {
            playSound(SoundEvents.WOOL_HIT, 0.5F, 1.0F);
        }
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        InteractionResult result = IRopeEntity.onDamageFrom(this, damageSource);

        if (result.consumesAction()) {
            destroyConnections(result == InteractionResult.SUCCESS);
            return true;
        }
        return false;
    }

    //Override Stuff

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos((double) Mth.floor(x) + 0.5D, y, (double) Mth.floor(z) + 0.5D);
    }

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0, EntityRegistry.HANGING_ROPE.get().getHeight(), 0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public @NotNull Vec3 getRopeHoldPosition(float f) {
        return getPosition(f).add(getLeashOffset());
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return EntityRegistry.HANGING_ROPE.get().getHeight() / 2;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void destroyConnections(boolean mayDrop) {
        if (connection != null) connection.destroy(mayDrop);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
