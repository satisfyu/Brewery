package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.entity.BrewKettleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DeactivateKettleS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        BlockPos blockPos = buf.readBlockPos();
        context.queue(() -> {
            Level level = context.getPlayer().getLevel();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof BrewKettleEntity brewKettleEntity) {

            }
        });
    }

}
