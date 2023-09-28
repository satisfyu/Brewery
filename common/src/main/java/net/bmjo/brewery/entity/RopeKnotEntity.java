package net.bmjo.brewery.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.registry.*;
import net.bmjo.brewery.util.rope.RopeConnection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RopeKnotEntity extends HangingEntity implements IRopeEntity {
    private static final int MAX_RANGE = 32;
    private static final byte GRACE_PERIOD = 100;
    private final Set<RopeConnection> connections = new HashSet<>();
    private final ObjectList<Tag> incompleteConnections = new ObjectArrayList<>();
    private int obstructionCheckTimer = 0;
    private byte graceTicks = GRACE_PERIOD;

    public RopeKnotEntity(EntityType<? extends RopeKnotEntity> entityType, Level world) {
        super(entityType, world);
    }

    private RopeKnotEntity(Level level, BlockPos blockPos) {
        super(EntityRegistry.HOP_ROPE_KNOT.get(), level, blockPos);
        setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static RopeKnotEntity create(@NotNull Level level, @NotNull BlockPos blockPos) {
        return new RopeKnotEntity(level, blockPos);
    }

    public Set<RopeConnection> getConnections() {
        return this.connections;
    }

    public void addConnection(@NotNull RopeConnection connection) {
        if (!connection.from().equals(connection.to())) {
            this.connections.add(connection);
        }
    }

    public boolean sameConnectionExist(@NotNull RopeConnection connection) {//TODO
        return this.connections.contains(connection);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) { //TODO DAMAGE
        ItemStack handStack = player.getItemInHand(interactionHand);
        if (this.getLevel().isClientSide()) {
            if (handStack.is(ObjectRegistry.HOP_ROPE.get())) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        boolean madeConnection = tryAttachHeldRope(player);
        if (madeConnection) {
            this.playPlacementSound();
            return InteractionResult.CONSUME;
        }

        boolean broke = false;
        for (RopeConnection connection : this.connections) {
            if (connection.to() == player) {
                broke = true;
                connection.destroy(true);
            }
        }
        if (broke) {
            return InteractionResult.CONSUME;
        }

        if (handStack.is(ObjectRegistry.HOP_ROPE.get())) {
            this.playPlacementSound();
            RopeConnection.create(this, player);
            if (!player.isCreative()) {
                handStack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }

        if (IRopeEntity.canDestroyWith(handStack)) {
            destroyConnections(!player.isCreative());
            graceTicks = 0;
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private boolean tryAttachHeldRope(Player player) {
        boolean hasMadeConnection = false;
        List<RopeConnection> attachableRopes = getHeldRopesInRange(player, position());
        for (RopeConnection connection : attachableRopes) {
            if (connection.from() == this) continue;

            RopeConnection newConnection = RopeConnection.create(connection.from(), this);

            if (newConnection != null) {
                connection.destroy(false);
                connection.removeSilently = true;
                hasMadeConnection = true;
            }
        }
        return hasMadeConnection;
    }

    private static List<RopeConnection> getHeldRopesInRange(Player player, Vec3 target) {
        AABB searchBox = AABB.ofSize(target, MAX_RANGE * 2, MAX_RANGE * 2, MAX_RANGE * 2);
        List<RopeKnotEntity> otherKnots = player.getLevel().getEntitiesOfClass(RopeKnotEntity.class, searchBox);

        List<RopeConnection> attachableRopes = new ArrayList<>();

        for (RopeKnotEntity source : otherKnots) {
            for (RopeConnection connection : source.getConnections()) {
                if (connection.to() != player) continue;
                attachableRopes.add(connection);
            }
        }
        return attachableRopes;
    }

    @Nullable
    public static RopeKnotEntity getHopRopeKnotEntity(Level level, BlockPos pos) {
        List<RopeKnotEntity> results = level.getEntitiesOfClass(RopeKnotEntity.class, AABB.ofSize(Vec3.atLowerCornerOf(pos), 2, 2, 2));

        for (RopeKnotEntity current : results) {
            if (new BlockPos(current.position()).equals(pos)) {
                return current;
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (getLevel().isClientSide()) {
            this.connections.removeIf(RopeConnection::dead);
            return;
        }
        checkOutOfWorld();

        boolean anyConverted = convertIncompleteConnections();
        updateConnections();
        removeDeadConnections();

        if (graceTicks < 0 || (anyConverted && incompleteConnections.isEmpty())) {
            graceTicks = 0;
        } else if (graceTicks > 0) {
            graceTicks--;
        }
    }

    private boolean convertIncompleteConnections() {
        if (!incompleteConnections.isEmpty()) {
            return incompleteConnections.removeIf(this::deserializeChainTag);
        }
        return false;
    }

    private void updateConnections() {
        double squaredMaxRange = MAX_RANGE * MAX_RANGE;
        for (RopeConnection connection : connections) {
            if (connection.dead()) continue;

            if (!this.isAlive()) {
                connection.destroy(true);
            } else if (connection.from() == this && connection.getSquaredDistance() > squaredMaxRange) {
                connection.destroy(true);
            }
        }

        if (obstructionCheckTimer++ == 100) {
            obstructionCheckTimer = 0;
            if (!survives()) {
                destroyConnections(true);
            }
        }
    }

    @Override
    public boolean survives() {
        BlockState blockState = getLevel().getBlockState(getPos());
        return blockState.is(BlockTags.FENCES) || blockState.is(Blocks.TRIPWIRE_HOOK);
    }

    private void removeDeadConnections() {
        boolean playBreakSound = false;
        for (RopeConnection connection : connections) {
            if (connection.needsBeDestroyed()) {
                connection.destroy(true);
            }
            if (connection.dead() && !connection.removeSilently) playBreakSound = true;
        }
        if (playBreakSound) dropItem(null);

        connections.removeIf(RopeConnection::dead);
        if (connections.isEmpty() && incompleteConnections.isEmpty() && graceTicks <= 0) {
            remove(RemovalReason.DISCARDED);
        }
    }

    public void destroyConnections(boolean mayDrop) {
        for (RopeConnection connection : connections) {
            connection.destroy(mayDrop);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        ListTag connectionTag = new ListTag();

        for (RopeConnection connection : connections) {
            if (connection.dead()) continue;
            if (connection.from() != this) continue;
            Entity toEntity = connection.to();
            CompoundTag compoundTag = new CompoundTag();
            if (toEntity instanceof Player) {
                UUID uuid = toEntity.getUUID();
                compoundTag.putUUID("UUID", uuid);
            } else if (toEntity instanceof RopeKnotEntity ropeKnotEntity) {
                BlockPos fromPos = this.getPos();
                BlockPos toPos = ropeKnotEntity.getPos();
                BlockPos relPos = toPos.subtract(fromPos);
                // Inverse rotation to store the position as 'facing' agnostic
                Direction inverseFacing = Direction.fromYRot(Direction.SOUTH.toYRot() - getYRot());
                relPos = getBlockPosAsFacingRelative(relPos, inverseFacing);
                compoundTag.putInt("RelX", relPos.getX());
                compoundTag.putInt("RelY", relPos.getY());
                compoundTag.putInt("RelZ", relPos.getZ());
            }
            connectionTag.add(compoundTag);
        }

        connectionTag.addAll(incompleteConnections);

        if (!connectionTag.isEmpty()) {
            nbt.put("chains", connectionTag);
        }
    }

    private BlockPos getBlockPosAsFacingRelative(BlockPos relPos, Direction facing) {
        Rotation rotation = Rotation.values()[facing.get2DDataValue()];
        return relPos.rotate(rotation);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("chains")) {
            incompleteConnections.addAll(nbt.getList("chains", Tag.TAG_COMPOUND));
        }
    }

    private boolean deserializeChainTag(Tag element) {
        if (element == null || getLevel().isClientSide()) {
            return true;
        }

        if (element instanceof CompoundTag tag) {
            if (tag.contains("UUID")) {
                UUID uuid = tag.getUUID("UUID");
                Entity toEntity = ((ServerLevel) getLevel()).getEntity(uuid);
                if (toEntity != null) {
                    RopeConnection.create(this, toEntity);
                    return true;
                }
            } else if (tag.contains("RelX") || tag.contains("RelY") || tag.contains("RelZ")) {
                BlockPos blockPos = new BlockPos(tag.getInt("RelX"), tag.getInt("RelY"), tag.getInt("RelZ"));
                // Adjust position to be relative to our facing direction
                blockPos = getBlockPosAsFacingRelative(blockPos, Direction.fromYRot(this.getYRot()));
                RopeKnotEntity entity = RopeKnotEntity.getHopRopeKnotEntity(getLevel(), blockPos.offset(this.getPos()));
                if (entity != null) {
                    RopeConnection.create(this, entity);
                    return true;
                }
            } else {
                Brewery.LOGGER.warn("Chain knot NBT is missing UUID or relative position.");
            }
        }

        if (graceTicks <= 0) {
            this.spawnAtLocation(ObjectRegistry.HOP_ROPE.get());
            dropItem(null);
            return true;
        }

        return false;
    }

    public boolean shouldRenderRope() {
        return true;
    }

    private double getYOffset() { //TODO
        BlockState blockState = this.level.getBlockState(this.blockPosition());
        VoxelShape shape = blockState.getShape(this.level, this.blockPosition());
        double y = shape.max(Direction.Axis.Y);
        return y - EntityRegistry.HOP_ROPE_KNOT.get().getHeight();
    }

    //OVERRIDE SHIT
    @Override
    public void setPos(double x, double y, double z) {
        super.setPos((double) Mth.floor(x) + 0.5D, (double) Mth.floor(y) + 0.5D, (double) Mth.floor(z) + 0.5D);
    }

    @Override
    protected void setDirection(Direction direction) {
        // AbstractDecorationEntity.facing should not be used
    }

    @Override
    public int getWidth() {
        return 9;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    @Override
    public void playPlacementSound() {
        playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void recalculateBoundingBox() {
        setPosRaw(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D); //TODO right height
        double w = getType().getWidth() / 2.0;
        double h = getType().getHeight();
        setBoundingBox(new AABB(getX() - w, getY(), getZ() - w, getX() + w, getY() + h, getZ() + w));
    }

    @Override
    public float mirror(Mirror mirror) {
        if (mirror != Mirror.NONE) {
            // Mirror the X axis, I am not sure why
            for (Tag element : incompleteConnections) {
                if (element instanceof CompoundTag tag) {
                    if (tag.contains("RelX")) {
                        tag.putInt("RelX", -tag.getInt("RelX"));
                    }
                }
            }
        }

        // Opposite of Entity.applyMirror, again I am not sure why, but it works
        float yaw = Mth.wrapDegrees(this.getYRot());
        return switch (mirror) {
            case LEFT_RIGHT -> 180 - yaw;
            case FRONT_BACK -> -yaw;
            default -> yaw;
        };
    }

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0, 4.5 / 16.0, 0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public @NotNull Vec3 getRopeHoldPosition(float f) {
        return getPosition(f).add(0, 4.5 / 16.0, 0);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 4.5f / 16f;
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.BLOCKS;
    }


    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this); //BreweryUtil.createEntitySpawnPacket(BreweryNetworking.SPAWN_KNOT_S2C_ID, this); //
    }
}
