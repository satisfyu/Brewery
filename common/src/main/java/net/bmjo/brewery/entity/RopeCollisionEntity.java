package net.bmjo.brewery.entity;

import net.bmjo.brewery.registry.EntityRegister;
import net.bmjo.brewery.util.HopRopeConnection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RopeCollisionEntity extends Entity implements RopeEntity {
    @Nullable
    private HopRopeConnection connection;

    public RopeCollisionEntity(EntityType<? extends RopeCollisionEntity> entityType, Level level) {
        super(entityType, level);
    }

    private RopeCollisionEntity(Level level, double x, double y, double z, @NotNull HopRopeConnection connection) {
        this(EntityRegister.ROPE_COLLISION.get(), level);
        this.connection = connection;
        this.setPos(x, y, z);
    }

    public static RopeCollisionEntity create(Level level, double x, double y, double z, HopRopeConnection connection) {
        return new RopeCollisionEntity(level, x, y, z, connection);
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

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
    public void tick() {
        if (getLevel().isClientSide()) return;
        if (connection != null && connection.needsBeDestroyed()) connection.destroy(true);

        if (connection == null || connection.dead()) {
            remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (RopeEntity.canDestroyWith(player.getItemInHand(interactionHand))) {
            destroyConnections(!player.isCreative());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
