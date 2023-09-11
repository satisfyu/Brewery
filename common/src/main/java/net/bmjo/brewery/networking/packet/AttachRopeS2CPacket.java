package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.client.RopeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class AttachRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int toId = buf.readInt();
        context.queue(() -> RopeHelper.createLink(Minecraft.getInstance(), fromId, toId));
    }
}
