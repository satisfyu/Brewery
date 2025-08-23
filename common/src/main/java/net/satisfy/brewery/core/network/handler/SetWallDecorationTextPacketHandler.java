package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.brewery.core.block.entity.WallDecorationBlockEntity;
import net.satisfy.brewery.core.network.packet.SetWallDecorationTextPacket;

public class SetWallDecorationTextPacketHandler implements NetworkManager.NetworkReceiver<SetWallDecorationTextPacket>{

    @Override
    public void receive(SetWallDecorationTextPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        Level level = player.level();
        if (level.isLoaded(packet.pos())) {
            BlockEntity entity = level.getBlockEntity(packet.pos());
            if (entity instanceof WallDecorationBlockEntity signEntity) {
                for (int i = 0; i < packet.texts().size() && i < 4; i++) {
                    signEntity.setText(i, Component.literal(packet.texts().get(i)));
                }
            }
        }
    }
}
