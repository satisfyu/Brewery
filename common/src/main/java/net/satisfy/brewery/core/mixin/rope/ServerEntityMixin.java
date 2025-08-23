package net.satisfy.brewery.core.mixin.rope;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.core.block.entity.rope.RopeKnotEntity;
import net.satisfy.brewery.core.network.packet.SyncRopeS2CPacket;
import net.satisfy.brewery.core.util.rope.RopeConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "sendPairingData", at = @At("TAIL"))
    private void sendPackages(ServerPlayer serverPlayer, Consumer<ClientboundCustomPayloadPacket> consumer, CallbackInfo ci) {
        if (this.entity instanceof RopeKnotEntity knot) {
            Set<RopeConnection> connections = knot.getConnections();
            IntList ids = new IntArrayList(connections.size());
            for (RopeConnection connection : connections) {
                if (connection.from() == knot) {
                    ids.add(connection.to().getId());
                }
            }
            if (!ids.isEmpty()) {
                consumer.accept(new ClientboundCustomPayloadPacket(new SyncRopeS2CPacket(knot.getId(), ids.toIntArray())));
            }
        }
    }
}
