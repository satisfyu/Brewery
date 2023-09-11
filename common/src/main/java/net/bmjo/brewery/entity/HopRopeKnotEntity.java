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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HopRopeKnotEntity extends Entity {
    private final Set<HopRopeConnection> connections = new HashSet<>();
    private final ObjectList<Tag> incompleteConnections = new ObjectArrayList<>();
    private int obstructionCheckTimer = 0;

    public HopRopeKnotEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    public BlockPos getPos() {
        return new BlockPos(this.position());
    }

    public void addConnection(@NotNull HopRopeConnection connection) {
        if (!connection.first().equals(connection.second())) {
            this.connections.add(connection);
        }
    }

    public Set<HopRopeConnection> getConnections() {
        return this.connections;
    }

    @Override
    public void tick() {
        if (getLevel().isClientSide()) {
            this.connections.removeIf(HopRopeConnection::dead);
            return;
        }
        checkOutOfWorld();

        convertIncompleteLinks();
        updateLinks();
        removeDeadLinks();
        super.tick();
    }

    private void updateLinks() {
        double squaredMaxRange = getMaxRange() * getMaxRange();
        for (HopRopeConnection connection : connections) {
            if (connection.dead()) continue;

            if (!isAlive()) {
                connection.destroy();
            } else if (connection.first() == this && connection.getSquaredDistance() > squaredMaxRange) {
                connection.destroy();
            }
        }

        if (obstructionCheckTimer++ == 100) {
            obstructionCheckTimer = 0;
            if (!canStayAttached()) {
                destroyLinks();
            }
        }
    }

    public void destroyLinks() {
        for (HopRopeConnection connection : connections) {
            connection.destroy();
        }
    }

    private boolean canStayAttached() {
        BlockState blockState = getLevel().getBlockState(getPos());
        return canAttachTo(blockState);
    }

    public static boolean canAttachTo(BlockState blockState) {
        return blockState != null && blockState.is(BlockTags.FENCES);
    }

    private void removeDeadLinks() {
        for (HopRopeConnection connection : connections) {
            if (connection.needsBeDestroyed()) connection.destroy();
            if (connection.dead()) playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
        }

        connections.removeIf(HopRopeConnection::dead);
        if (connections.isEmpty() && incompleteConnections.isEmpty()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    private static int getMaxRange() {
        return 32;
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
            playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
        }

        boolean broke = false;
        for (HopRopeConnection connection : this.connections) {
            if (connection.second() == player) {
                broke = true;
                connection.destroy();
            }
        }
        if (broke) {
            return InteractionResult.CONSUME;
        }

        if (handStack.is(ObjectRegistry.HOP_ROPE.get())) {
            // Interacted with a valid chain item, create a new link
            playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
            HopRopeConnection.create(this, player);
            if (!player.isCreative()) {
                handStack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }
        playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
        return super.interact(player, interactionHand);
    }

    public boolean tryAttachHeldRope(Player player) {
        boolean hasMadeConnection = false;
        List<HopRopeConnection> attachableLinks = getHeldRopesInRange(player, position());
        for (HopRopeConnection connection : attachableLinks) {
            if (connection.first() == this) continue;

            HopRopeConnection newConnection = HopRopeConnection.create(connection.first(), this);

            if (newConnection != null) {
                connection.destroy();
                hasMadeConnection = true;
                placeHangingRopes(this.level, newConnection);
            }
        }
        return hasMadeConnection;
    }

    public static List<HopRopeConnection> getHeldRopesInRange(Player player, Vec3 target) {
        AABB searchBox = AABB.ofSize(target, getMaxRange() * 2, getMaxRange() * 2, getMaxRange() * 2);
        List<HopRopeKnotEntity> otherKnots = player.getLevel().getEntitiesOfClass(HopRopeKnotEntity.class, searchBox);

        List<HopRopeConnection> attachableLinks = new ArrayList<>();

        for (HopRopeKnotEntity source : otherKnots) {
            for (HopRopeConnection connection : source.getConnections()) {
                if (connection.second() != player) continue;
                // Knot is connected to the player
                attachableLinks.add(connection);
            }
        }
        return attachableLinks;
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

    public static HopRopeKnotEntity create(@NotNull Level level, @NotNull BlockPos blockPos) {
        HopRopeKnotEntity hopRopeKnotEntity = new HopRopeKnotEntity(EntityRegister.HOP_ROPE_KNOT.get(), level);
        hopRopeKnotEntity.moveTo(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f, 0.0F, 0.0F);
        level.addFreshEntity(hopRopeKnotEntity);
        return hopRopeKnotEntity;
    }

    public boolean sameConnectionExist(@NotNull HopRopeConnection connection) {
        return this.connections.contains(connection);
    }

    public static void placeHangingRopes(Level level, HopRopeConnection connection) {
        List<BlockPos> crossingBlocks = BreweryMath.bresenham(connection);
        for (BlockPos blockPos : crossingBlocks) {
            if (level.getBlockState(blockPos).isAir() && BreweryMath.isCollinear(blockPos, connection, 1.0)) {
                level.setBlock(blockPos, Blocks.OAK_FENCE.defaultBlockState(), 3);
            }
        }
    }

    //OVERRIDE SHIT
    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0, 4.5 / 16, 0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Vec3 getRopeHoldPosition(float f) {
        return getPosition(f).add(0, 4.5 / 16, 0);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 4.5f / 16f;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.BLOCKS;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        discard();
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("chains")) {
            incompleteConnections.addAll(nbt.getList("chains", Tag.TAG_COMPOUND));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        ListTag linksTag = new ListTag();

        // Write complete links
        for (HopRopeConnection connection : connections) {
            if (connection.dead()) continue;
            if (connection.first() != this) continue;
            Entity second = connection.second();
            CompoundTag compoundTag = new CompoundTag();
            if (second instanceof Player) {
                UUID uuid = second.getUUID();
                compoundTag.putUUID("UUID", uuid);
            } else if (second instanceof HopRopeKnotEntity hopRopeKnotEntity) {
                BlockPos relPos = hopRopeKnotEntity.getPos();
                compoundTag.putInt("RelX", relPos.getX());
                compoundTag.putInt("RelY", relPos.getY());
                compoundTag.putInt("RelZ", relPos.getZ());
            }
            linksTag.add(compoundTag);
        }

        linksTag.addAll(incompleteConnections);

        if (!linksTag.isEmpty()) {
            nbt.put("chains", linksTag);
        }
    }

    private void convertIncompleteLinks() {
        if (!incompleteConnections.isEmpty()) {
            incompleteConnections.removeIf(this::deserializeChainTag);
        }
    }

    private boolean deserializeChainTag(Tag element) {
        if (element == null || getLevel().isClientSide()) {
            return true;
        }

        assert element instanceof CompoundTag;
        CompoundTag tag = (CompoundTag) element;

        if (tag.contains("UUID")) {
            UUID uuid = tag.getUUID("UUID");
            Entity second = ((ServerLevel) getLevel()).getEntity(uuid);
            if (second != null) {
                HopRopeConnection.create(this, second);
                return true;
            }
        } else if (tag.contains("RelX") || tag.contains("RelY") || tag.contains("RelZ")) {
            BlockPos blockPos = new BlockPos(tag.getInt("RelX"), tag.getInt("RelY"), tag.getInt("RelZ"));
            // Adjust position to be relative to our facing direction
            HopRopeKnotEntity second = HopRopeKnotEntity.getHopRopeKnotEntity(getLevel(), blockPos);
            System.out.println(second);
            if (second != null) {
                HopRopeConnection.create(this, second);
                return true;
            }
        } else {
            Brewery.LOGGER.warn("Chain knot NBT is missing UUID or relative position."); //TODO
        }
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
