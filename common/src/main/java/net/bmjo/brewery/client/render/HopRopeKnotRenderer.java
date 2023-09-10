package net.bmjo.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.bmjo.brewery.client.HopRopeHelper;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Set;

public class HopRopeKnotRenderer extends EntityRenderer<HopRopeKnotEntity> {
    private final HopRopeRenderer hopRopeRenderer = new HopRopeRenderer();

    public HopRopeKnotRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(HopRopeKnotEntity entity, Frustum frustum, double d, double e, double f) {
        if (entity.noCulling) return true;
        for (HopRopeConnection connection : entity.getConnections()) {
            if (connection.first() != entity) continue;
            if (connection.second() instanceof Player) return true;
            else if (connection.second().shouldRender(d, e, f)) return true;
        }
        return super.shouldRender(entity, frustum, d, e, f);
    }

    @Override
    public void render(HopRopeKnotEntity hopRopeKnotEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        Set<HopRopeConnection> connections = hopRopeKnotEntity.getConnections();
        for (HopRopeConnection connection : connections) {
            if (connection.first() != hopRopeKnotEntity || connection.dead()) continue;
            this.renderChainLink(connection, g, poseStack, multiBufferSource);
            //this.drawDebugVector(poseStack, hopRopeKnotEntity, connection.second(), multiBufferSource.getBuffer(RenderType.LINES));
        }
        super.render(hopRopeKnotEntity, f, g, poseStack, multiBufferSource, i);
    }

    private void renderChainLink(HopRopeConnection connection, float tickDelta, PoseStack poseStack, MultiBufferSource vertexConsumerProvider) {
        HopRopeKnotEntity fromEntity = connection.first();
        Entity toEntity = connection.second();
        poseStack.pushPose();

        // Don't have to lerp knot position as it can't move
        // Also lerping the position of an entity that was just created
        // causes visual bugs because the position is lerped from 0/0/0.
        Vec3 srcPos = fromEntity.position().add(fromEntity.getLeashOffset());
        Vec3 dstPos = toEntity.getRopeHoldPosition(tickDelta);

        // The leash pos offset
        Vec3 leashOffset = fromEntity.getLeashOffset();
        poseStack.translate(leashOffset.x, leashOffset.y, leashOffset.z);

        // Some further performance improvements can be made here:
        // Create a rendering layer that:
        // - does not have normals
        // - does not have an overlay
        // - does not have vertex color
        // - uses a tri strip instead of quads
        RenderType entityCutout = RenderType.entityCutoutNoCull(new BreweryIdentifier("textures/block/rope.png"));
        VertexConsumer buffer = vertexConsumerProvider.getBuffer(entityCutout);

        Vec3 offset = HopRopeHelper.getChainOffset(srcPos, dstPos);
        poseStack.translate(offset.x(), 0, offset.z());

        // Now we gather light information for the chain. Since the chain is lighter if there is more light.
        BlockPos blockPosOfStart = ofFloored(fromEntity.getEyePosition(tickDelta));
        BlockPos blockPosOfEnd = ofFloored(toEntity.getEyePosition(tickDelta));
        int blockLightLevelOfStart = fromEntity.getLevel().getBrightness(LightLayer.BLOCK, blockPosOfStart);
        int blockLightLevelOfEnd = toEntity.getLevel().getBrightness(LightLayer.BLOCK, blockPosOfEnd);
        int skylightLevelOfStart = fromEntity.getLevel().getBrightness(LightLayer.SKY, blockPosOfStart);
        int skylightLevelOfEnd = toEntity.getLevel().getBrightness(LightLayer.SKY, blockPosOfEnd);

        Vec3 startPos = srcPos.add(offset.x(), 0, offset.z());
        Vec3 endPos = dstPos.add(-offset.x(), 0, -offset.z());
        Vector3f chainVec = new Vector3f((float) (endPos.x - startPos.x), (float) (endPos.y - startPos.y), (float) (endPos.z - startPos.z));

        float angleY = -(float) Math.atan2(chainVec.z(), chainVec.x());

        Quaternionf quaternionf = new Quaternionf(0, 0, 0, 1).rotateXYZ(0, angleY, 0);
        poseStack.mulPose(new Quaternion(quaternionf.x(), quaternionf.y(), quaternionf.z(), quaternionf.w()));

        hopRopeRenderer.renderRope(buffer, poseStack, chainVec, blockLightLevelOfStart, blockLightLevelOfEnd, skylightLevelOfStart, skylightLevelOfEnd);

        poseStack.popPose();
    }

    private ResourceLocation getChainTexture(Item item) {
        ResourceLocation id = Registry.ITEM.getKey(item);
        return new ResourceLocation(id.getNamespace(), "textures/block/" + id.getPath() + ".png");
    }

    public static BlockPos ofFloored(Vec3 vec) {
        return ofFloored(vec.x(), vec.y(), vec.z());
    }

    public static BlockPos ofFloored(double x, double y, double z) {
        return new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    private void drawDebugVector(PoseStack matrices, Entity fromEntity, Entity toEntity, VertexConsumer buffer) {
        if (toEntity == null) return;
        Matrix4f modelMat = matrices.last().pose();
        Vec3 vec = toEntity.position().subtract(fromEntity.position());
        Vec3 normal = vec.normalize();
        buffer.vertex(modelMat, 0, 1, 0)
                .color(0, 255, 0, 255)
                .normal((float) normal.x, (float) normal.y, (float) normal.z).endVertex();
        buffer.vertex(modelMat, (float) vec.x, (float) vec.y + 1, (float) vec.z)
                .color(255, 0, 0, 255)
                .normal((float) normal.x, (float) normal.y, (float) normal.z).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(HopRopeKnotEntity entity) {
        return null;
    }
}
