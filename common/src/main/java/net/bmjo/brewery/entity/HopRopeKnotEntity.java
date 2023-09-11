package net.bmjo.brewery.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.registry.EntityRegister;
import net.bmjo.brewery.registry.ObjectRegistry;
import net.bmjo.brewery.util.BreweryMath;
import net.bmjo.brewery.util.HopRopeConnection;
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
import net.minecraft.world.entity.*;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HopRopeKnotEntity extends HangingEntity {
    private static final int MAX_RANGE = 32;
    private final Set<HopRopeConnection> connections = new HashSet<>();
    private final ObjectList<Tag> incompleteConnections = new ObjectArrayList<>();
    private int obstructionCheckTimer = 0;

    public HopRopeKnotEntity(EntityType<? extends  HopRopeKnotEntity> entityType, Level world) {
        super(entityType, world);
    }

    private HopRopeKnotEntity(EntityType<? extends HopRopeKnotEntity> entityType, Level level, BlockPos blockPos) {
        this(entityType, level);
        setPos((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D);
    }

    public static HopRopeKnotEntity create(@NotNull Level level, @NotNull BlockPos blockPos) {
        HopRopeKnotEntity hopRopeKnotEntity = new HopRopeKnotEntity(EntityRegister.HOP_ROPE_KNOT.get(), level, blockPos);
        level.addFreshEntity(hopRopeKnotEntity);
        return hopRopeKnotEntity;
    }

    public Set<HopRopeConnection> getConnections() {
        return this.connections;
    }

    public void addConnection(@NotNull HopRopeConnection connection) {
        if (!connection.from().equals(connection.to())) {
            this.connections.add(connection);
        }
    }

    public boolean sameConnectionExist(@NotNull HopRopeConnection connection) {
        return this.connections.contains(connection);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
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
        for (HopRopeConnection connection : this.connections) {
            if (connection.to() == player) {
                broke = true;
                connection.destroy();
            }
        }
        if (broke) {
            return InteractionResult.CONSUME;
        }

        if (handStack.is(ObjectRegistry.HOP_ROPE.get())) {
            this.playPlacementSound();
            HopRopeConnection.create(this, player);
            if (!player.isCreative()) {
                handStack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }
        this.playPlacementSound();
        return super.interact(player, interactionHand);
    }

    private boolean tryAttachHeldRope(Player player) {
        boolean hasMadeConnection = false;
        List<HopRopeConnection> attachableRopes = getHeldRopesInRange(player, position());
        for (HopRopeConnection connection : attachableRopes) {
            if (connection.from() == this) continue;

            HopRopeConnection newConnection = HopRopeConnection.create(connection.from(), this);

            if (newConnection != null) {
                connection.destroy();
                hasMadeConnection = true;
                placeHangingRopes(this.level, newConnection);
            }
        }
        return hasMadeConnection;
    }

    private static List<HopRopeConnection> getHeldRopesInRange(Player player, Vec3 target) { //TODO lass das auf item raufmachen maybe und auf dem spieler speichern weil kb zu suchen
        AABB searchBox = AABB.ofSize(target, MAX_RANGE * 2, MAX_RANGE * 2, MAX_RANGE * 2);
        List<HopRopeKnotEntity> otherKnots = player.getLevel().getEntitiesOfClass(HopRopeKnotEntity.class, searchBox);

        List<HopRopeConnection> attachableRopes = new ArrayList<>();

        for (HopRopeKnotEntity source : otherKnots) {
            for (HopRopeConnection connection : source.getConnections()) {
                if (connection.to() != player) continue;
                attachableRopes.add(connection);
            }
        }
        return attachableRopes;
    }

    @Nullable
    public static HopRopeKnotEntity getHopRopeKnotEntity(Level level, BlockPos pos) {
        List<HopRopeKnotEntity> results = level.getEntitiesOfClass(HopRopeKnotEntity.class, AABB.ofSize(Vec3.atLowerCornerOf(pos), 2, 2, 2));

        for (HopRopeKnotEntity current : results) {
            if (new BlockPos(current.position()).equals(pos)) {
                return current;
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (getLevel().isClientSide()) {
            this.connections.removeIf(HopRopeConnection::dead);
            return;
        }
        checkOutOfWorld();

        convertIncompleteConnections();
        updateConnections();
        removeDeadConnections();
        super.tick();
    }

    private void convertIncompleteConnections() {
        if (!incompleteConnections.isEmpty()) {
            incompleteConnections.removeIf(this::deserializeChainTag);
        }
    }

    private void updateConnections() {
        double squaredMaxRange = MAX_RANGE * MAX_RANGE;
        for (HopRopeConnection connection : connections) {
            if (connection.dead()) continue;

            if (!this.isAlive()) {
                connection.destroy();
            } else if (connection.from() == this && connection.getSquaredDistance() > squaredMaxRange) {
                connection.destroy();
            }
        }

        if (obstructionCheckTimer++ == 100) {
            obstructionCheckTimer = 0;
            if (!survives()) {
                destroyConnections();
            }
        }
    }

    @Override
    public boolean survives() {
        BlockState blockState = getLevel().getBlockState(getPos());
        return blockState.is(BlockTags.FENCES);
    }

    private void removeDeadConnections() {
        for (HopRopeConnection connection : connections) {
            if (connection.needsBeDestroyed()) connection.destroy();
            if (connection.dead()) playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
        }

        connections.removeIf(HopRopeConnection::dead);
        if (connections.isEmpty() && incompleteConnections.isEmpty()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    private void destroyConnections() {
        for (HopRopeConnection connection : connections) {
            connection.destroy();
        }
    }

    private static void placeHangingRopes(Level level, HopRopeConnection connection) {
        List<BlockPos> crossingBlocks = BreweryMath.bresenham(connection);
        for (BlockPos blockPos : crossingBlocks) {
            if (level.getBlockState(blockPos).isAir() && BreweryMath.isCollinear(blockPos, connection, 1.0)) {
                level.setBlock(blockPos, Blocks.OAK_FENCE.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        ListTag connectionTag = new ListTag();

        for (HopRopeConnection connection : connections) {
            if (connection.dead()) continue;
            if (connection.from() != this) continue;
            Entity toEntity = connection.to();
            CompoundTag compoundTag = new CompoundTag();
            if (toEntity instanceof Player) {
                UUID uuid = toEntity.getUUID();
                compoundTag.putUUID("UUID", uuid);
            } else if (toEntity instanceof HopRopeKnotEntity hopRopeKnotEntity) {
                BlockPos fromPos = this.getPos();
                BlockPos toPos = hopRopeKnotEntity.getPos();
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
                    HopRopeConnection.create(this, toEntity);
                    return true;
                }
            } else if (tag.contains("RelX") || tag.contains("RelY") || tag.contains("RelZ")) {
                BlockPos blockPos = new BlockPos(tag.getInt("RelX"), tag.getInt("RelY"), tag.getInt("RelZ"));
                // Adjust position to be relative to our facing direction
                blockPos = getBlockPosAsFacingRelative(blockPos, Direction.fromYRot(this.getYRot()));
                HopRopeKnotEntity entity = HopRopeKnotEntity.getHopRopeKnotEntity(getLevel(), blockPos.offset(this.getPos()));
                if (entity != null) {
                    HopRopeConnection.create(this, entity);
                    return true;
                }
            } else {
                Brewery.LOGGER.warn("Chain knot NBT is missing UUID or relative position."); //TODO
            }
        }

        return false;
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

    }

    @Override
    public void playPlacementSound() {
        playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void recalculateBoundingBox() {
        setPosRaw(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
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
        return new Vec3(0, 4.5 / 16, 0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public @NotNull Vec3 getRopeHoldPosition(float f) {
        return getPosition(f).add(0, 4.5 / 16, 0);
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
    protected void defineSynchedData() {

    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        discard();
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
