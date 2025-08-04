package net.satisfy.brewery.core.network.packet;


import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.satisfy.brewery.core.effect.alcohol.MotionBlur;

public class DrunkEffectS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        Minecraft client = Minecraft.getInstance();
        boolean activate = buf.readBoolean();
        client.execute(activate ? MotionBlur::activate : MotionBlur::deactivate);
    }
}

