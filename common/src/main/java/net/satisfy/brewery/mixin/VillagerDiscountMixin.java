package net.satisfy.brewery.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.satisfy.brewery.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Villager.class)
public class VillagerDiscountMixin {

    @Inject(method = "updateSpecialPrices", at = @At("HEAD"))
    private void applyHasteDiscount(Player player, CallbackInfo ci) {
        if (player.hasEffect(MobEffectRegistry.PINTCHARISMA)) {
            Villager villager = (Villager) (Object) this;
            for (MerchantOffer offer : villager.getOffers()) {
                int discountLevel = Objects.requireNonNull(player.getEffect(MobEffectRegistry.PINTCHARISMA)).getAmplifier();
                double discountForEffect = 0.1 * (discountLevel + 1);
                int discount = (int) Math.floor(discountForEffect * offer.getBaseCostA().getCount());

                offer.addToSpecialPriceDiff(-Math.max(discount, 1));
            }
        }
    }
}