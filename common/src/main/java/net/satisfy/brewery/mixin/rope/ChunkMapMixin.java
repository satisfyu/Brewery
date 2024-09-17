package net.satisfy.brewery.mixin.rope;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkMap.class)
public class ChunkMapMixin { //TODO MAYBE
    // I don't need to port an empty mixin right?...

    @Unique
    private ServerPlayer brewery$serverPlayer;
    @Unique
    private LevelChunk brewery$levelChunk;

    public ChunkMapMixin(LevelChunk levelChunk) {
        this.brewery$levelChunk = levelChunk;
    }

//    @Inject(method = "playerLoadedChunk", at = @At(value = "HEAD"))
//    private void sendAttachChainPackets(ServerPlayer serverPlayer, MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject, LevelChunk levelChunk, CallbackInfo ci) {
//        this.brewery$serverPlayer = serverPlayer;
//        this.brewery$levelChunk = levelChunk;
//
//    }
//
//    @Redirect(method = "playerLoadedChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;chunkPosition()Lnet/minecraft/world/level/ChunkPos;"))
//    private ChunkPos sendAttachChainPackets(Entity entity) {
//        List<RopeKnotEntity> knots = Lists.newArrayList();
//        if (entity.chunkPosition().equals(brewery$levelChunk.getPos())) {
//            if (entity instanceof RopeKnotEntity knot && !knot.getConnections().isEmpty()) {
//                knots.add(knot);
//            }
//        }
//        for (RopeKnotEntity knot : knots) {
//            FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
//            Set<RopeConnection> connections = knot.getConnections();
//            IntList ids = new IntArrayList(connections.size());
//            for (RopeConnection connection : connections) {
//                if (connection.from() == knot) {
//                    ids.add(connection.to().getId());
//                }
//            }
//            if (!ids.isEmpty()) {
//                buf.writeInt(knot.getId());
//                buf.writeIntIdList(ids);
//                //NetworkManager.sendToPlayer(brewery$serverPlayer, BreweryNetworking.SYNC_ROPE_S2C_ID, buf);
//            }
//        }
//        return entity.chunkPosition();
//    }
}
