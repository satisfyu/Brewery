package net.satisfy.brewery.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.player.LocalPlayer;
import net.satisfy.brewery.networking.packet.SyncRequestC2SPacket;

public class PlayerJoinEvent implements ClientPlayerEvent.ClientPlayerJoin {
    @Override
    public void join(LocalPlayer player) {
        SyncRequestC2SPacket packet = new SyncRequestC2SPacket(true);
        NetworkManager.sendToServer(packet);
    }
}
