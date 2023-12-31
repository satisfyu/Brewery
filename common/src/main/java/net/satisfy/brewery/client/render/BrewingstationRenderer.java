package net.satisfy.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.satisfy.brewery.block.brewingstation.BrewingstationBlock;
import net.satisfy.brewery.entity.BrewstationBlockEntity;
import net.satisfy.brewery.util.BreweryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingstationRenderer implements BlockEntityRenderer<BrewstationBlockEntity> {
    public BrewingstationRenderer(BlockEntityRendererProvider.Context ctx) {

    }

    @Override
    public void render(BrewstationBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if (!blockEntity.hasLevel()) {
            return;
        }
        BlockState selfState = blockEntity.getBlockState();
        if (selfState.getBlock() instanceof BrewingstationBlock) {
            List<ItemStack> ingredients = blockEntity.getIngredient();
            poseStack.pushPose();
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.translate(0.5f, 0.2F, 0.5f);
            for (ItemStack itemStack : ingredients) {
                poseStack.translate(0.0f, 0.2f, 0.0f);
                BreweryUtil.renderItem(itemStack, poseStack, multiBufferSource, blockEntity);
            }
            poseStack.popPose();
        }
    }
}