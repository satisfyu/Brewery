package net.satisfy.brewery.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.block.entity.rope.HangingRopeEntity;
import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.effect.alcohol.MotionBlur;
import net.satisfy.brewery.networking.packet.*;
import net.satisfy.brewery.registry.MobEffectRegistry;
import net.satisfy.brewery.util.rope.RopeHelper;

import static net.satisfy.brewery.networking.packet.DetachRopeS2CPacket.removeConnections;

public class BreweryNetworking {

    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SyncRequestC2SPacket.PACKET_ID, SyncRequestC2SPacket.PACKET_CODEC, (payload, context) -> {
            context.queue(() -> {
                ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();
                if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
                    AlcoholManager.syncAlcohol(serverPlayer, alcoholPlayer.getAlcohol());
                }
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, DrinkAlcoholC2SPacket.PACKET_ID, DrinkAlcoholC2SPacket.PACKET_CODEC, (payload, context) -> {
            context.queue(() ->{
                ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();
                if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
                    AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
                    alcoholLevel.drink();

                    serverPlayer.addEffect(new MobEffectInstance(MobEffectRegistry.DRUNK, AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
                    if (alcoholLevel.isBlackout()) {
                        if (!serverPlayer.hasEffect(MobEffectRegistry.BLACKOUT)) {
                            serverPlayer.addEffect(new MobEffectInstance(MobEffectRegistry.BLACKOUT, 15 * 20, 0, false, false));
                            serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 13 * 20, 0, false, false));
                        }
                    }

                    AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
                }
            });
        });
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, AlcoholSyncS2CPacket.PACKET_ID, AlcoholSyncS2CPacket.PACKET_CODEC, (payload, context) -> {
            LocalPlayer localPlayer = (LocalPlayer) context.getPlayer();
            int drunkenness = payload.drunkenness();
            int immunity = payload.immunity();

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
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, AttachRopeS2CPacket.PACKET_ID, AttachRopeS2CPacket.PACKET_CODEC, (payload, context) -> {
            int fromId = payload.fromId();
            int toId = payload.toId();

            context.queue(() -> RopeHelper.createConnection(Minecraft.getInstance(), fromId, toId));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ChangeHangingRopeS2CPacket.PACKET_ID, ChangeHangingRopeS2CPacket.PACKET_CODEC, (payload, context) -> {
            int id = payload.id();
            boolean active = payload.active();

            context.queue(() -> {
                Entity entity = context.getPlayer().level().getEntity(id);
                if (entity instanceof HangingRopeEntity hangingRope) {
                    hangingRope.setActive(active);
                } else {
                    Brewery.LOGGER.debug("Cant change Hanging Rope because Entity {} (#{}) is not a hanging rope entity.", entity, id);
                }
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DetachRopeS2CPacket.PACKET_ID, DetachRopeS2CPacket.PACKET_CODEC, (payload, context) -> {
            int fromId = payload.fromId();
            int toId = payload.toId();

            context.queue(() -> removeConnections(Minecraft.getInstance(), fromId, toId));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, DrunkEffectS2CPacket.PACKET_ID, DrunkEffectS2CPacket.PACKET_CODEC, (payload, context) -> {
            boolean active = payload.activate();

            context.queue(() -> {
                Minecraft client = Minecraft.getInstance();
                client.execute(active ? MotionBlur::activate : MotionBlur::deactivate);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncRopeS2CPacket.PACKET_ID, SyncRopeS2CPacket.PACKET_CODEC, (payload, context) -> {
            int fromId = payload.fromId();
            int[] toIds = payload.toIds();

            context.queue(() -> context.queue(() -> RopeHelper.createConnections(Minecraft.getInstance(), fromId, toIds)));
        });
    }
}
