package net.satisfy.brewery.core.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Villager.class)
public class VillagerDiscountMixin {

    @Inject(method = "updateSpecialPrices", at = @At("HEAD"))
    private void applyHasteDiscount(Player player, CallbackInfo ci) {
        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.PINTCHARISMA.get()))) {
            Villager villager = (Villager) (Object) this;
            for (MerchantOffer offer : villager.getOffers()) {
                int discountLevel = Objects.requireNonNull(player.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.PINTCHARISMA.get()))).getAmplifier();
                double discountForEffect = 0.1 * (discountLevel + 1);
                int discount = (int) Math.floor(discountForEffect * offer.getBaseCostA().getCount());

                offer.addToSpecialPriceDiff(-Math.max(discount, 1));
            }
        }
    }
}