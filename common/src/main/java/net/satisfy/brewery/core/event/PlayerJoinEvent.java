package net.satisfy.brewery.core.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.player.LocalPlayer;

public class PlayerJoinEvent implements ClientPlayerEvent.ClientPlayerJoin {
    @Override
    public void join(LocalPlayer player) {
        // TODO fixme
        //NetworkManager.sendToServer(new SyncRequestC2SPacket());
    }
}
