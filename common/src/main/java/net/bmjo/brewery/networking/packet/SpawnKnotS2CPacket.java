package net.bmjo.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.bmjo.brewery.client.RopeHelper;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.BreweryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class SpawnKnotS2CPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        EntityType<?> entityType = buf.readById(Registry.ENTITY_TYPE);
        int entityId = buf.readVarInt();
        UUID uuid = buf.readUUID();
        Vec3 pos = BreweryUtil.readVec3(buf);

        context.queue(() -> {
            Minecraft client = Minecraft.getInstance();
            Entity entity = RopeHelper.createEntity(client, entityType, uuid, entityId, pos);
            if (entity == null) return;
            if (entity instanceof HopRopeKnotEntity knot) {
                knot.setGraceTicks((byte) 0);
            }
            assert client.level != null;
            client.level.putNonPlayerEntity(entityId, entity);
        });
    }
}
