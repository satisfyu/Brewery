package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.player.LocalPlayer;
import net.satisfy.brewery.core.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.effect.alcohol.MotionBlur;
import net.satisfy.brewery.core.network.packet.AlcoholSyncS2CPacket;

public class AlcoholSyncS2CPacketHandler implements NetworkManager.NetworkReceiver<AlcoholSyncS2CPacket> {

    @Override
    public void receive(AlcoholSyncS2CPacket packet, NetworkManager.PacketContext context) {
        LocalPlayer localPlayer = (LocalPlayer) context.getPlayer();
        int drunkenness = packet.drunkenness();
        int immunity = packet.immunity();
        context.queue(() -> {
            if (localPlayer instanceof AlcoholPlayer alcoholPlayer) {
                alcoholPlayer.brewery$setAlcohol(new AlcoholLevel(drunkenness, immunity));
                if (alcoholPlayer.brewery$getAlcohol().isDrunk()) {
                    MotionBlur.activate();
                }
                if (!alcoholPlayer.brewery$getAlcohol().isDrunk()) {
                    MotionBlur.deactivate();
                }
            }
        });
    }
}
