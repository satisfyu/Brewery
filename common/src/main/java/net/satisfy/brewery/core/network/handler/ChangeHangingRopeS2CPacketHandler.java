package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.core.block.entity.rope.HangingRopeEntity;
import net.satisfy.brewery.core.network.packet.ChangeHangingRopeS2CPacket;

public class ChangeHangingRopeS2CPacketHandler implements NetworkManager.NetworkReceiver<ChangeHangingRopeS2CPacket>{

    @Override
    public void receive(ChangeHangingRopeS2CPacket packet, NetworkManager.PacketContext context) {
        int id = packet.id();
        boolean active = packet.active();
        context.queue(() -> {
            Entity entity = context.getPlayer().level().getEntity(id);
            if (entity instanceof HangingRopeEntity hangingRope) {
                hangingRope.setActive(active);
            }
        });

    }
}
