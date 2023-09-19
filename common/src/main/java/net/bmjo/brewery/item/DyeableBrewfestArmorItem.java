package net.bmjo.brewery.item;


import net.bmjo.brewery.util.BreweryClientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class DyeableBrewfestArmorItem extends DyeableArmorItem implements IBrewfestArmorSet {
    private final int defaultColor;
    public DyeableBrewfestArmorItem(ArmorMaterial material, EquipmentSlot slot, int color, Properties settings) {
        super(material, slot, settings);
        defaultColor = color;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide()) {
            if (entity instanceof Player player) {
                checkForSet(player);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTagElement("display");
        return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : this.defaultColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("tooltip.brewery.dyeable").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
        if (world != null && world.isClientSide()) {
            BreweryClientUtil.appendTooltip(tooltip);
        }
    }
}