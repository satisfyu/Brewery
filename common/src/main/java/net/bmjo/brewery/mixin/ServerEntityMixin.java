package net.bmjo.brewery.mixin;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
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
    private void sendPackages(Consumer<Packet<?>> consumer, CallbackInfo ci) {
        if (this.entity instanceof HopRopeKnotEntity knot) {
            FriendlyByteBuf buf = BreweryNetworking.createPacketBuf();
            Set<HopRopeConnection> connections = knot.getConnections();
            IntList ids = new IntArrayList(connections.size());
            for (HopRopeConnection connection : connections) {
                if (connection.from() == knot) {
                    ids.add(connection.to().getId());
                }
            }
            if (!ids.isEmpty()) {
                buf.writeInt(knot.getId());
                buf.writeVarIntArray(ids.toIntArray());
                consumer.accept(createPlayC2SPacket(BreweryNetworking.SYNC_ROPE_S2C_ID, buf));
            }
        }
    }

    private static Packet<ClientGamePacketListener> createPlayC2SPacket(ResourceLocation channel, FriendlyByteBuf buf) {
        return new ClientboundCustomPayloadPacket(channel, buf);
    }
}
