package net.satisfy.brewery.item.armor;

import de.cristelknight.doapi.common.item.CustomArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.satisfy.brewery.registry.ArmorRegistry;

import java.util.List;


public class BrewfestChest extends CustomArmorItem {
    public BrewfestChest(Holder<ArmorMaterial> material, Properties settings) {
        super(material, Type.CHESTPLATE, settings);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        ArmorRegistry.appendTooltip(list);
    }
}