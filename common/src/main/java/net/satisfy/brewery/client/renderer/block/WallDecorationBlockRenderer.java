package net.satisfy.brewery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.satisfy.brewery.core.block.WallDecorationBlock;
import net.satisfy.brewery.core.block.entity.WallDecorationBlockEntity;

public class WallDecorationBlockRenderer implements BlockEntityRenderer<WallDecorationBlockEntity> {

    public WallDecorationBlockRenderer() {
    }

    @Override
    public void render(WallDecorationBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);
        Direction facing = entity.getBlockState().getValue(WallDecorationBlock.FACING);
        float rotation = facing == Direction.NORTH ? 180f : facing == Direction.WEST ? -90f : facing == Direction.EAST ? 90f : 0f;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
        poseStack.translate(0, -0.45, -0.425);
        poseStack.scale(0.01f, -0.01f, 0.01f);
        Font font = Minecraft.getInstance().font;
        boolean glow = entity.isGlowing();
        Component text = entity.getText(0);
        String str = text.getString();
        if (str.length() > 8) str = str.substring(0, 8);
        if (!str.isEmpty()) {
            poseStack.pushPose();
            if (glow) {
                var seeThroughBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
                font.drawInBatch(Component.literal(str).getVisualOrderText(), -font.width(str) / 2f, 0, 0xFADFB0, false, poseStack.last().pose(), seeThroughBuffer, Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
                seeThroughBuffer.endBatch();
            } else {
                font.drawInBatch(Component.literal(str).getVisualOrderText(), -font.width(str) / 2f, 0, 0xFADFB0, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
