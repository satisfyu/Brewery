package net.bmjo.brewery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IngredientItem extends Item {

    public IngredientItem(Properties settings) {
        super(settings);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("tooltip.brewery.ingredient").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

}