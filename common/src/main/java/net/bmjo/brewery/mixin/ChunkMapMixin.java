package net.bmjo.brewery.mixin;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Unique
    private ServerPlayer brewery$serverPlayer;
    @Unique
    private LevelChunk brewery$levelChunk;

    public ChunkMapMixin(LevelChunk levelChunk) {
        this.brewery$levelChunk = levelChunk;
    }

    @Inject(method = "playerLoadedChunk", at = @At(value = "HEAD"))
    private void sendAttachChainPackets(ServerPlayer serverPlayer, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, LevelChunk levelChunk, CallbackInfo ci) {
        this.brewery$serverPlayer = serverPlayer;
        this.brewery$levelChunk = levelChunk;

    }

    @Redirect(method = "playerLoadedChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;chunkPosition()Lnet/minecraft/world/level/ChunkPos;"))
    private ChunkPos sendAttachChainPackets(Entity entity) {
        List<HopRopeKnotEntity> knots = Lists.newArrayList();
        if (entity.chunkPosition().equals(brewery$levelChunk.getPos())) {
            if (entity instanceof HopRopeKnotEntity knot && !knot.getConnections().isEmpty()) {
                knots.add(knot);
            }
        }
        for (HopRopeKnotEntity knot : knots) {
            FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
            Set<HopRopeConnection> connections = knot.getConnections();
            IntList ids = new IntArrayList(connections.size());
            for (HopRopeConnection connection : connections) {
                if (connection.first() == knot) {
                    ids.add(connection.second().getId());
                }
            }
            if (!ids.isEmpty()) {
                buf.writeInt(knot.getId());
                buf.writeIntIdList(ids);
                //NetworkManager.sendToPlayer(brewery$serverPlayer, BreweryNetworking.SYNC_ROPE_S2C_ID, buf);
            }
        }
        return entity.chunkPosition();
    }
}
