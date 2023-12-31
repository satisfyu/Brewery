package net.satisfy.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.entity.rope.HangingRopeEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ChangeHangingRopeS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int id = buf.readInt();
        boolean active = buf.readBoolean();
        context.queue(() -> {
            Entity entity = context.getPlayer().getLevel().getEntity(id);
            if (entity instanceof HangingRopeEntity hangingRope) {
                hangingRope.setActive(active);
            } else {
                Brewery.LOGGER.debug("Cant change Hanging Rope because Entity {} (#{}) is not a hanging rope entity.", entity, id);
            }
        });

    }
}
