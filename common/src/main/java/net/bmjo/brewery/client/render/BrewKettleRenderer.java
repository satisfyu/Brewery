package net.bmjo.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.bmjo.brewery.block.brewingstation.BrewingstationBlock;
import net.bmjo.brewery.entity.BrewstationEntity;
import net.bmjo.brewery.util.BreweryUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewKettleRenderer implements BlockEntityRenderer<BrewstationEntity> {
    public BrewKettleRenderer(BlockEntityRendererProvider.Context ctx) {

    }
    @Override
    public void render(BrewstationEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if (!blockEntity.hasLevel()) {
            return;
        }
        BlockState selfState = blockEntity.getBlockState();
        if (selfState.getBlock() instanceof BrewingstationBlock) {
            List<ItemStack> ingredients = blockEntity.getIngredient();
            poseStack.pushPose();
            for (ItemStack itemStack : ingredients) {
                poseStack.translate(0f, 0.4f, 0f);
                BreweryUtil.renderItem(itemStack, poseStack, multiBufferSource, blockEntity);
            }
            poseStack.popPose();
        }
    }
}