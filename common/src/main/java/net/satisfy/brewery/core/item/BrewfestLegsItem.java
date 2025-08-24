package net.satisfy.brewery.core.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.satisfy.brewery.core.registry.ArmorRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BrewfestLegsItem extends ArmorItem {
    private final ResourceLocation leggingsTexture;

    public BrewfestLegsItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties, ResourceLocation leggingsTexture) {
        super(armorMaterial, type, properties);
        this.leggingsTexture = leggingsTexture;
    }

    public ResourceLocation getLeggingsTexture() {
        return leggingsTexture;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.LEGS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        Level world = Minecraft.getInstance().level;
        if (world != null && world.isClientSide()) {
            ArmorRegistry.appendToolTip(tooltip);
        }
    }
}
