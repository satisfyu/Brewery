package net.satisfy.brewery.core.network.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.core.block.entity.rope.HangingRopeEntity;

public class ChangeHangingRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int id = buf.readInt();
        boolean active = buf.readBoolean();
        context.queue(() -> {
            Entity entity = context.getPlayer().level().getEntity(id);
            if (entity instanceof HangingRopeEntity hangingRope) {
                hangingRope.setActive(active);
            }
        });

    }
}
