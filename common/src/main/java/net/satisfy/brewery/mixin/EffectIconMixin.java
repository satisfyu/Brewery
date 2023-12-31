package net.satisfy.brewery.mixin;

import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.effect.DrunkEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectIconMixin {
    @Redirect(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;"))
    private Collection<MobEffectInstance> getWithoutDrunkEffect(LocalPlayer player) {
        Collection<MobEffectInstance> effects = player.getActiveEffects();
        if (player instanceof AlcoholPlayer alcoholPlayer) {
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            effects.removeIf((effect) -> effect.getEffect() instanceof DrunkEffect && !alcoholLevel.isDrunk());
        }
        return effects;
    }
}
