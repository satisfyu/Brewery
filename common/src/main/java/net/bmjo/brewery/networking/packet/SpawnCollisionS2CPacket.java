package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.client.RopeHelper;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class SpawnCollisionS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        System.out.println("collision");
        int entityTypeID = buf.readVarInt();
        EntityType<?> entityType = Registry.ENTITY_TYPE.byId(entityTypeID);
        UUID uuid = buf.readUUID();
        int entityId = buf.readVarInt();
        Vec3 pos = BreweryUtil.readVec3(buf);

        context.queue(() -> {
            Minecraft client = Minecraft.getInstance();
            Entity e = RopeHelper.createEntity(client, entityType, uuid, entityId, pos);
            if (e == null) return;
            assert client.level != null;
            client.level.putNonPlayerEntity(entityId, e);
        });
    }
}
