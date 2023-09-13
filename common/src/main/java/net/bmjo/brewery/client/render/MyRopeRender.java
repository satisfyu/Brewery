package net.bmjo.brewery.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.bmjo.brewery.util.HopRopeConnection;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MyRopeRender {
    private static final int SIZE = 1;
    private static final Vec3 INVERT = new Vec3(-1.0F, -1.0F, -1.0F);
    private static final Vec3 POSITIVE_X = new Vec3(1.0F, 0.0F, 0.0F);
    private static final Vec3 POSITIVE_Z = new Vec3(0.0F, 1.0F, 0.0F);

    public static void rendermy(HopRopeConnection connection, PoseStack poseStack) {
        poseStack.pushPose();
        HopRopeKnotEntity from = connection.from();
        Entity to = connection.to();
        Vec3 ropeVec = to.position().subtract(from.position());

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        createModel(ropeVec, poseStack, buffer, false);
        createModel(ropeVec, poseStack, buffer, true);

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, new BreweryIdentifier("textures/block/rope.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        tesselator.end();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    private static void createModel(Vec3 ropeVec, PoseStack poseStack, BufferBuilder buffer, boolean vertical) {
        Vec3 ropeNormal = ropeVec.normalize();
        Vec3 horVec = ropeNormal.equals(POSITIVE_Z) || ropeNormal.equals(POSITIVE_Z.multiply(INVERT)) ? POSITIVE_X : ropeNormal.cross(POSITIVE_Z).normalize();
        Vec3 verVec = ropeNormal.cross(horVec).multiply(INVERT).normalize();
        Vec3 crossVec = vertical ? verVec : horVec;

        poseStack.pushPose();

        poseStack.translate(-crossVec.x / 2, -crossVec.y / 2, -crossVec.z / 2);
        Matrix4f positionMatrix = poseStack.last().pose();

        buffer.vertex(positionMatrix, (float) crossVec.x, (float) crossVec.y, (float) crossVec.z).color(-1).uv(3 / 16f, 0f).endVertex();
        buffer.vertex(positionMatrix, 0, 0, 0).color(-1).uv(0f, 0f).endVertex();
        buffer.vertex(positionMatrix, (float) ropeVec.x, (float) ropeVec.y, (float) ropeVec.z).color(-1).uv(0f, 1f).endVertex();
        buffer.vertex(positionMatrix, (float) (ropeVec.x + crossVec.x), (float) (ropeVec.y + crossVec.y), (float) (ropeVec.z + crossVec.z)).color(-1).uv(3 / 16f, 1f).endVertex();

        poseStack.popPose();
    }
}
