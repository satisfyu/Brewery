package net.satisfy.brewery.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.entity.RopeKnotBlockEntity;

public class RopeKnotRenderer implements BlockEntityRenderer<RopeKnotBlockEntity> {
    @Override
    public void render(RopeKnotBlockEntity be, float pt, PoseStack ps, MultiBufferSource buf, int packedLight, int overlay) {
        if (be.getLevel() == null) return;
        BlockState held = be.getHeldBlock();
        if (held == null || held.isAir()) return;

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();

        ps.pushPose();
        brd.renderBatched(held, be.getBlockPos(), be.getLevel(), ps, buf.getBuffer(RenderType.cutout()), false, RandomSource.create());
        ps.popPose();
    }
}
