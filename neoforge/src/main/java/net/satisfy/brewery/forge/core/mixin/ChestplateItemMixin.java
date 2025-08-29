package net.satisfy.brewery.forge.core.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.satisfy.brewery.core.item.BrewfestChestItem;
import net.satisfy.brewery.core.registry.ArmorRegistry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(BrewfestChestItem.class)
public abstract class ChestplateItemMixin extends ArmorItem {
    @Shadow
    @Final
    private ResourceLocation chestplateTexture;

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(
                new IClientItemExtensions() {
                    @Override
                    @NotNull
                    public Model getGenericArmorModel(@NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
                        return ArmorRegistry.getChestplateModel(itemStack.getItem(), original.body, original.leftArm, original.rightArm, original.leftLeg, original.rightLeg);
                    }
                }
        );
    }

    @Override
    @NotNull
    public ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return chestplateTexture;
    }

    private ChestplateItemMixin(Holder<ArmorMaterial> armorMaterial, Type armorType, Properties itemProperties) {
        super(armorMaterial, armorType, itemProperties);
    }
}
