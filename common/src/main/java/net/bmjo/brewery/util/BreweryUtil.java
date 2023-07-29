package net.bmjo.brewery.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BreweryUtil {
    private static final String BLOCKPOS_KEY ="blockposes";
    public static int power(int base, int exponent) {
        int value = 1;
        for(int i = 0; i < exponent; i++) {
            value *= base;
        }
        return value;
    }

    public static void putBlockPos(CompoundTag compoundTag, Collection<BlockPos> blockPoses) {
        if (blockPoses == null || blockPoses.size() == 0) return;
        int[] positions = new int[blockPoses.size() * 3];
        int pos = 0;
        for (BlockPos blockPos : blockPoses) {
            positions[pos * 3] = blockPos.getX();
            positions[pos * 3 + 1] = blockPos.getY();
            positions[pos * 3 + 2] = blockPos.getZ();
            pos++;
        }
        compoundTag.putIntArray(BLOCKPOS_KEY, positions);
    }


    public static Set<BlockPos> readBlockPos(CompoundTag compoundTag) {
        int[] positions = compoundTag.getIntArray(BLOCKPOS_KEY);
        Set<BlockPos> blockSet = new HashSet<>();
        for (int pos = 0; pos < positions.length / 3; pos++) {
            blockSet.add(new BlockPos(positions[pos * 3], positions[pos * 3 + 1], positions[pos * 3 + 2]));
        }
        return blockSet;
    }

    public static int getLightLevel(Level world, BlockPos pos) {
        int bLight = world.getBrightness(LightLayer.BLOCK, pos);
        int sLight = world.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    public static <T extends BlockEntity> void renderItem(ItemStack itemStack, PoseStack poseStack, MultiBufferSource multiBufferSource, T blockEntity) {
        Level level = blockEntity.getLevel();
        if (level != null) {
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.GUI, getLightLevel(level, blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, 1);
        }
    }
}
