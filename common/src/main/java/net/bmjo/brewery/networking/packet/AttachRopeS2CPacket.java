package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.util.rope.RopeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class AttachRopeS2CPacket implements NetworkManager.NetworkReceiver {
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int toId = buf.readInt();
        context.queue(() -> RopeHelper.createConnection(Minecraft.getInstance(), fromId, toId));
    }
}
