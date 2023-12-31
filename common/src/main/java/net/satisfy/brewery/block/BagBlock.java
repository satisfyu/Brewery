package net.satisfy.brewery.block;

import de.cristelknight.doapi.common.block.FacingBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;

import java.util.List;

public class BagBlock extends FacingBlock {
    public BagBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("tooltip.brewery.crafting").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.brewery.canbeplaced").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

    }
}
