package net.bmjo.brewery.util;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.bmjo.brewery.item.IBrewfestArmorSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BreweryClientUtil {
    public static <T extends BlockEntity> void renderBlock(BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, T entity) {
        Minecraft.getInstance()
                .getBlockRenderer()
                .renderSingleBlock(state, matrices, vertexConsumers, BreweryUtil.getLightLevel(entity.getLevel(), entity.getBlockPos()), OverlayTexture.NO_OVERLAY);
    }

    public static void appendTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable(  "tooltip.brewfest.brewfestline1").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(  "tooltip.brewfest.brewfestline2").withStyle(ChatFormatting.BLUE));

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        boolean helmet = IBrewfestArmorSet.hasBrewfestHelmet(player);
        boolean breastplate = IBrewfestArmorSet.hasBrewfestBreastplate(player);
        boolean leggings = IBrewfestArmorSet.hasBrewfestLeggings(player);
        boolean boots = IBrewfestArmorSet.hasBrewfestBoots(player);

        tooltip.add(Component.nullToEmpty(""));
        tooltip.add(Component.translatable("tooltip.brewfest.brewfest_set").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(helmet ? Component.translatable("tooltip.brewfest.brewfesthelmet").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfesthelmet").withStyle(ChatFormatting.GRAY));
        tooltip.add(breastplate ? Component.translatable("tooltip.brewfest.brewfestbreastplate").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfestbreastplate").withStyle(ChatFormatting.GRAY));
        tooltip.add(leggings ? Component.translatable("tooltip.brewfest.brewfestleggings").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfestleggings").withStyle(ChatFormatting.GRAY));
        tooltip.add(boots ? Component.translatable("tooltip.brewfest.brewfestboots").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfestboots").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.nullToEmpty(""));
        tooltip.add(Component.translatable("tooltip.brewfest.brewfest_seteffect").withStyle(ChatFormatting.GRAY));
        tooltip.add(helmet && breastplate && leggings && boots ? Component.translatable("tooltip.brewfest.brewfest_effect").withStyle(ChatFormatting.DARK_GREEN) : Component.translatable("tooltip.brewfest.brewfest_effect").withStyle(ChatFormatting.GRAY));
    }

    public static void registerColorArmor(Item item, int defaultColor) {
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> tintIndex > 0 ? -1 : getColor(stack, defaultColor), item);
    }

    private static int getColor(ItemStack itemStack, int defaultColor) {
        CompoundTag displayTag = itemStack.getTagElement("display");
        if (displayTag != null && displayTag.contains("color", Tag.TAG_ANY_NUMERIC))
            return displayTag.getInt("color");
        return defaultColor;
    }

}