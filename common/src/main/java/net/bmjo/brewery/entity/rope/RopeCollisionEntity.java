package net.bmjo.brewery.entity.rope;

import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.util.rope.RopeConnection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RopeCollisionEntity extends Entity implements IRopeEntity {
    @Nullable
    private RopeConnection connection;

    public RopeCollisionEntity(EntityType<? extends RopeCollisionEntity> entityType, Level level) {
        super(entityType, level);
    }

    private RopeCollisionEntity(Level level, double x, double y, double z, @NotNull RopeConnection connection) {
        this(EntityRegistry.ROPE_COLLISION.get(), level);
        this.connection = connection;
        this.setPos(x, y, z);
    }

    public static RopeCollisionEntity create(Level level, double x, double y, double z, RopeConnection connection) {
        return new RopeCollisionEntity(level, x, y, z, connection);
    }

    @Override
    public void tick() {
        if (getLevel().isClientSide()) return;
        if (connection != null && connection.needsBeDestroyed()) connection.destroy(true);

        if (connection == null || connection.dead()) {
            remove(Entity.RemovalReason.DISCARDED);
        }
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

    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isHolding((itemStack) -> itemStack.is(Items.SHEARS))) {
            return super.shouldRenderAtSqrDistance(d);
        } else {
            return false;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void destroyConnections(boolean mayDrop) {
        if (connection != null) connection.destroy(mayDrop);
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
        return new ClientboundAddEntityPacket(this); //BreweryUtil.createEntitySpawnPacket(BreweryNetworking.SPAWN_COLLISION_S2C_ID, this);
    }
}
