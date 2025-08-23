package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.satisfy.brewery.core.network.packet.SyncRopeS2CPacket;
import net.satisfy.brewery.core.util.rope.RopeHelper;

public class SyncRopeS2CPacketHandler implements NetworkManager.NetworkReceiver<SyncRopeS2CPacket> {

    @Override
    public void receive(SyncRopeS2CPacket packet, NetworkManager.PacketContext context) {
        int fromId = packet.fromId();
        int[] toIds = packet.toIds();
        context.queue(() -> RopeHelper.createConnections(Minecraft.getInstance(), fromId, toIds));
    }
}
