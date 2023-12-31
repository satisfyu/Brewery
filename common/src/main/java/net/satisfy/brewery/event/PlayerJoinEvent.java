package net.satisfy.brewery.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.satisfy.brewery.networking.BreweryNetworking;
import net.minecraft.client.player.LocalPlayer;

public class PlayerJoinEvent implements ClientPlayerEvent.ClientPlayerJoin {
    @Override
    public void join(LocalPlayer player) {
        NetworkManager.sendToServer(BreweryNetworking.ALCOHOL_SYNC_REQUEST_C2S_ID, BreweryNetworking.createPacketBuf());
    }
}
