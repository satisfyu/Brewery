package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.effect.alcohol.AlcoholLevel;
import net.bmjo.brewery.effect.alcohol.AlcoholPlayer;
import net.bmjo.brewery.effect.alcohol.MotionBlur;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;

public class AlcoholSyncS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        LocalPlayer localPlayer = (LocalPlayer) context.getPlayer();
        int drunkenness = buf.readInt();
        int immunity = buf.readInt();
        context.queue(() -> {
            if (localPlayer instanceof AlcoholPlayer alcoholPlayer) {
                alcoholPlayer.setAlcohol(new AlcoholLevel(drunkenness, immunity));
                if (alcoholPlayer.getAlcohol().isDrunk()) {
                    MotionBlur.activate();
                }
                if (!alcoholPlayer.getAlcohol().isDrunk()) {
                    MotionBlur.deactivate();
                }
            }
        });
    }
}
