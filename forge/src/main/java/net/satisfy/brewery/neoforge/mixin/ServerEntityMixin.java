package net.satisfy.brewery.neoforge.mixin;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.bundle.PacketAndPayloadAcceptor;
import net.satisfy.brewery.block.entity.rope.RopeKnotEntity;
import net.satisfy.brewery.networking.packet.SyncRopeS2CPacket;
import net.satisfy.brewery.util.rope.RopeConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Set;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "sendPairingData", at = @At("TAIL"))
    private void brewery$sendPackages(ServerPlayer arg, PacketAndPayloadAcceptor<ClientGamePacketListener> packetAndPayloadAcceptor, CallbackInfo ci) {
        if (this.entity instanceof RopeKnotEntity knot) {
            Set<RopeConnection> connections = knot.getConnections();
            ArrayList<Integer> ids = new ArrayList<>();
            for (RopeConnection connection : connections) {
                if (connection.from() == knot) {
                    ids.add(connection.to().getId());
                }
            }

            int[] idsArray = ids.stream().mapToInt(i -> i).toArray();
            if (!ids.isEmpty()) {
                SyncRopeS2CPacket packet = new SyncRopeS2CPacket(knot.getId(), idsArray);
                packetAndPayloadAcceptor.accept(new ClientboundCustomPayloadPacket(packet));
            }
        }
    }
}