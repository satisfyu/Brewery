package net.bmjo.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.bmjo.brewery.client.RopeHelper;
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
            if (connection.from() != entity) continue;
            if (connection.to() instanceof Player) return true;
            else if (connection.to().shouldRender(d, e, f)) return true;
        }
        return super.shouldRender(entity, frustum, d, e, f);
    }

    @Override
    public void render(HopRopeKnotEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        Set<HopRopeConnection> connections = entity.getConnections();
        for (HopRopeConnection connection : connections) {
            if (connection.from() != entity || connection.dead()) continue;
            this.renderChainLink(connection, g, poseStack, multiBufferSource);
        }
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }

    private void renderChainLink(HopRopeConnection connection, float tickDelta, PoseStack poseStack, MultiBufferSource vertexConsumerProvider) {
        MyRopeRender.rendermy(connection, poseStack);
        HopRopeKnotEntity fromKnot = connection.from();
        Entity toEntity = connection.to();
        poseStack.pushPose();

        Vec3 fromPos = fromKnot.position().add(fromKnot.getLeashOffset());
        Vec3 toPos = toEntity.getRopeHoldPosition(tickDelta);

        Vec3 leashOffset = fromKnot.getLeashOffset();
        poseStack.translate(leashOffset.x, leashOffset.y, leashOffset.z);

        RenderType entityCutout = RenderType.entityCutoutNoCull(new BreweryIdentifier("textures/block/rope.png"));
        VertexConsumer buffer = vertexConsumerProvider.getBuffer(entityCutout);

        Vec3 offset = RopeHelper.getChainOffset(fromPos, toPos);
        poseStack.translate(offset.x(), 0, offset.z());

        BlockPos blockPosOfStart = ofFloored(fromKnot.getEyePosition(tickDelta));
        BlockPos blockPosOfEnd = ofFloored(toEntity.getEyePosition(tickDelta));
        int blockLightLevelOfStart = fromKnot.getLevel().getBrightness(LightLayer.BLOCK, blockPosOfStart);
        int blockLightLevelOfEnd = toEntity.getLevel().getBrightness(LightLayer.BLOCK, blockPosOfEnd);
        int skylightLevelOfStart = fromKnot.getLevel().getBrightness(LightLayer.SKY, blockPosOfStart);
        int skylightLevelOfEnd = toEntity.getLevel().getBrightness(LightLayer.SKY, blockPosOfEnd);

        Vec3 startPos = fromPos.add(offset.x(), 0, offset.z());
        Vec3 endPos = toPos.add(-offset.x(), 0, -offset.z());
        Vector3f chainVec = new Vector3f((float) (endPos.x - startPos.x), (float) (endPos.y - startPos.y), (float) (endPos.z - startPos.z));

        float angleY = -(float) Math.atan2(chainVec.z(), chainVec.x());

        Quaternionf quaternionf = new Quaternionf(0, 0, 0, 1).rotateXYZ(0, angleY, 0);
        poseStack.mulPose(new Quaternion(quaternionf.x(), quaternionf.y(), quaternionf.z(), quaternionf.w()));

        //hopRopeRenderer.renderRope(buffer, poseStack, chainVec, blockLightLevelOfStart, blockLightLevelOfEnd, skylightLevelOfStart, skylightLevelOfEnd);
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

    @Override
    public ResourceLocation getTextureLocation(HopRopeKnotEntity entity) {
        return null;
    }
}
