package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.satisfy.brewery.core.network.packet.AttachRopeS2CPacket;
import net.satisfy.brewery.core.util.rope.RopeHelper;

public class AttachRopeS2CPacketHandler implements NetworkManager.NetworkReceiver<AttachRopeS2CPacket> {

    @Override
    public void receive(AttachRopeS2CPacket packet, NetworkManager.PacketContext context) {
        int fromId = packet.fromId();
        int toId = packet.toId();
        context.queue(() -> RopeHelper.createConnection(Minecraft.getInstance(), fromId, toId));
    }
}
