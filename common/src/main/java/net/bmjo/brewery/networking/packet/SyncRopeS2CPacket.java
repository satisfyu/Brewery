package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.util.rope.RopeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class SyncRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int fromId = buf.readInt();
        int[] toIds = buf.readVarIntArray();
        context.queue(() -> RopeHelper.createLinks(Minecraft.getInstance(), fromId, toIds));
    }
}
