package net.bmjo.brewery.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void afterRender(PoseStack none, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        PoseStack matrixStack = new PoseStack();
        Vec3 targetPosition = new Vec3(0, 100, 0);
        Vec3 transformedPosition = targetPosition.subtract(camera.getPosition());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot() + 180.0F));

        matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(45));
        matrixStack.translate(-transformedPosition.x, -transformedPosition.y, -transformedPosition.z);

        matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);
        Matrix4f positionMatrix = matrixStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        buffer.vertex(positionMatrix, 0, 1, 0).color(1f, 1f, 1f, 1f).uv(0f, 0f).endVertex();
        buffer.vertex(positionMatrix, 0, 0, 0).color(1f, 0f, 0f, 1f).uv(0f, 1f).endVertex();
        buffer.vertex(positionMatrix, 1, 0, 0).color(0f, 1f, 0f, 1f).uv(1f, 1f).endVertex();
        buffer.vertex(positionMatrix, 1, 1, 0).color(0f, 0f, 1f, 1f).uv(1f, 0f).endVertex();

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, new BreweryIdentifier("icon.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();

        tesselator.end();
        RenderSystem.enableCull();
    }
}
