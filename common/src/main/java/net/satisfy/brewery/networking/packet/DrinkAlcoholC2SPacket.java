package net.satisfy.brewery.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.registry.EffectRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DrinkAlcoholC2SPacket implements NetworkManager.NetworkReceiver {
    @Override
    public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();
        if (serverPlayer instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            alcoholLevel.drink();

            serverPlayer.addEffect(new MobEffectInstance(EffectRegistry.DRUNK.get(), AlcoholManager.DRUNK_TIME, alcoholLevel.getDrunkenness() - 1, false, alcoholLevel.isDrunk()));
            if (alcoholLevel.isBlackout()) {
                if (!serverPlayer.hasEffect(EffectRegistry.BLACKOUT.get())) {
                    serverPlayer.addEffect(new MobEffectInstance(EffectRegistry.BLACKOUT.get(), 15 * 20, 0, false, false));
                    serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 13 * 20, 0, false, false));
                }
            }

            AlcoholManager.syncAlcohol(serverPlayer, alcoholLevel);
        }
    }
}
