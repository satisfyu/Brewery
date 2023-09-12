package net.bmjo.brewery.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.bmjo.brewery.alcohol.AlcoholPlayer;
import net.bmjo.brewery.alcohol.AlcoholManager;
import net.bmjo.brewery.client.BreweryClient;
import net.bmjo.brewery.registry.EffectRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Environment(EnvType.CLIENT)
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;getClearColorScale()F"))
    private static float drunkColor(ClientLevel.ClientLevelData levelData) {
        Player player = BreweryClient.getPlayer();
        if (player instanceof AlcoholPlayer alcoholPlayer && alcoholPlayer.getAlcohol().isBlackout()) {
            MobEffectInstance effect = player.getEffect(EffectRegistry.BLACKOUT.get());
            if (effect != null && effect.getDuration() <= AlcoholManager.BEGIN_TIME) {
                if (effect.getDuration() < 10) {
                    return  1.0F - (float)effect.getDuration() / 10.0F;
                } else {
                    return 0.0F;
                }
            }
        }
        return levelData.getClearColorScale();
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
    private static void drunkFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
        Entity entity = camera.getEntity();
        if (entity instanceof LivingEntity livingEntity && entity instanceof AlcoholPlayer alcoholPlayer && alcoholPlayer.getAlcohol().isBlackout()) {
            MobEffectInstance effect = livingEntity.getEffect(EffectRegistry.BLACKOUT.get());
            if (effect != null) {
                int time = effect.getDuration();
                if (time <= AlcoholManager.BEGIN_TIME) {
                    float distance = blinkEffect(time);
                    RenderSystem.setShaderFogStart(fogMode == FogRenderer.FogMode.FOG_SKY ? 0.0F : distance * 0.75F);
                    RenderSystem.setShaderFogEnd(distance);
                    RenderSystem.setShaderFogShape(FogShape.SPHERE);
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private static float blinkEffect(int time) {
        float scale = 70f;
        int distance = 5;
        float value = distance * power(time / (scale - 8) - 3, 2) * power(time / scale - 1, 10);
        return Mth.clamp(value, 0.0F, 64.0F);
    }

    @Unique
    private static float power(float base, int exponent) {
        float value = 1;
        for(int i = 0; i < exponent; i++) {
            value *= base;
        }
        return value;
    }
}
