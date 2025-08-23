package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.satisfy.brewery.core.effect.alcohol.MotionBlur;
import net.satisfy.brewery.core.network.packet.DrunkEffectS2CPacket;

public class DrunkEffectS2CPacketHandler implements NetworkManager.NetworkReceiver<DrunkEffectS2CPacket>{

    @Override
    public void receive(DrunkEffectS2CPacket packet, NetworkManager.PacketContext context) {
        Minecraft client = Minecraft.getInstance();
        boolean activate = packet.activate();
        client.execute(activate ? MotionBlur::activate : MotionBlur::deactivate);
    }
}
