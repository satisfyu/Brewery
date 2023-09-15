package net.bmjo.brewery.mixin;

import com.mojang.blaze3d.platform.Window;
import net.bmjo.brewery.effect.alcohol.MotionBlur;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "onFramebufferResize", at = @At("TAIL"))
    private void updateShaderSize(CallbackInfo ci) {
        if (MotionBlur.enabled) MotionBlur.shader.resize(MotionBlur.client.getWindow().getWidth(), MotionBlur.client.getWindow().getHeight());
    }
}