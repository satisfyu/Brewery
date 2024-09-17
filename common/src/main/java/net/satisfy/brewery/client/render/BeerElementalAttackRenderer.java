package net.satisfy.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.entity.BeerElementalAttackEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BeerElementalAttackRenderer extends EntityRenderer<BeerElementalAttackEntity> {

    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, "textures/particle/beer_elemental_attack.png");

    private static final float SCALE = 0.4F;
    private static final Vec2[] UVS = new Vec2[]{new Vec2(1F, 1F), new Vec2(0F, 1F), new Vec2(0F, 0F), new Vec2(1F, 0F)};
    private static final Vector3f[] VERTS = new Vector3f[]{new Vector3f(0.5F, -0.5F, 0.0F), new Vector3f(-0.5F, -0.5F, 0.0F), new Vector3f(-0.5F, 0.5F, 0.0F), new Vector3f(0.5F, 0.5F, 0.0F)};

    public BeerElementalAttackRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer vertexBuilder, int combinedLight) {
        Matrix4f poseMatrix = pose.pose();
        for (int i = 0; i < 4; i++) {
            Vector3f localPos = VERTS[i];
            Vec2 quadUvs = UVS[i];

            Vector4f pos = new Vector4f(localPos.x(), localPos.y() + 0.5F, localPos.z(), 1.0F);
            pos.mul(poseMatrix);

            vertexBuilder.addVertex(pos.x(), pos.y(), pos.z(),
                    1, 1.0F, 1.0F, 1,
                    combinedLight,
                    0F, 1F, 0F
            );
        }
    }

    @Override
    protected int getBlockLightLevel(BeerElementalAttackEntity entity, BlockPos blockPos) {
        return super.getBlockLightLevel(entity, blockPos);
    }

    @Override
    public void render(BeerElementalAttackEntity entity, float f, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        if (entity.tickCount < 2 && entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)
            return;

        poseStack.pushPose();

        poseStack.scale(SCALE, SCALE, SCALE);

        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        render(poseStack.last(), buffer.getBuffer((RenderType.entityTranslucentCull(TEXTURE_LOCATION))), combinedLight);

        poseStack.popPose();

        super.render(entity, f, partialTick, poseStack, buffer, combinedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BeerElementalAttackEntity entity) {
        return TEXTURE_LOCATION;
    }
}