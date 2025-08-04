package net.satisfy.brewery.core.network.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.satisfy.brewery.core.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.effect.alcohol.MotionBlur;

public class AlcoholSyncS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        LocalPlayer localPlayer = (LocalPlayer) context.getPlayer();
        int drunkenness = buf.readInt();
        int immunity = buf.readInt();
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
