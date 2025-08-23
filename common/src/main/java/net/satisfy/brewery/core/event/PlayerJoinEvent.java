package net.satisfy.brewery.core.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.player.LocalPlayer;
import net.satisfy.brewery.core.network.packet.SyncRequestC2SPacket;

public class PlayerJoinEvent implements ClientPlayerEvent.ClientPlayerJoin {
    @Override
    public void join(LocalPlayer player) {
        // TODO fixme
        //NetworkManager.sendToServer(new SyncRequestC2SPacket());
    }
}
