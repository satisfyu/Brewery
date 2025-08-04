package net.satisfy.brewery.core.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.player.LocalPlayer;
import net.satisfy.brewery.core.network.BreweryNetworking;

public class PlayerJoinEvent implements ClientPlayerEvent.ClientPlayerJoin {
    @Override
    public void join(LocalPlayer player) {
        NetworkManager.sendToServer(BreweryNetworking.ALCOHOL_SYNC_REQUEST_C2S_ID, BreweryNetworking.createPacketBuf());
    }
}
