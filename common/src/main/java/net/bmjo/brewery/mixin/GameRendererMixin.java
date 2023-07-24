package net.bmjo.brewery.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bmjo.brewery.alcohol.MotionBlur;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;tryTakeScreenshotIfNeeded()V"))
    public void renderMotionBlur(CallbackInfo ci) {
        if (MotionBlur.enabled && MotionBlur.shader != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            MotionBlur.shader.process(MotionBlur.client.getFrameTime() - 10);
        }
    }
}
