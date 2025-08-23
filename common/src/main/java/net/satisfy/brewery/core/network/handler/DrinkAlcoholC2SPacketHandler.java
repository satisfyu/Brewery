package net.satisfy.brewery.core.network.handler;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.satisfy.brewery.core.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.network.packet.DrinkAlcoholC2SPacket;
import net.satisfy.brewery.core.registry.MobEffectRegistry;

public class DrinkAlcoholC2SPacketHandler implements NetworkManager.NetworkReceiver<DrinkAlcoholC2SPacket> {

    @Override
    public void receive(DrinkAlcoholC2SPacket drinkAlcoholC2SPacket, NetworkManager.PacketContext packetContext) {
        ServerPlayer serverPlayer = (ServerPlayer) packetContext.getPlayer();
        if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.brewery$getAlcohol();
            alcoholLevel.drink();

            serverPlayer.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.DRUNK.get()), AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
            if (alcoholLevel.isBlackout()) {
                if (!serverPlayer.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()))) {
                    serverPlayer.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()), 15 * 20, 0, false, false));
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 13 * 20, 0, false, false));
                }
            }

            AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
        }
    }
}
