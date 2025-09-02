package net.satisfy.brewery.client.renderer.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.satisfy.brewery.core.item.BrewfestBootsItem;
import net.satisfy.brewery.core.registry.ArmorRegistry;

public class BrewfestBootsRenderer {
    public void render(PoseStack poseStack, MultiBufferSource buffer, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
        if (slot == EquipmentSlot.FEET && stack.getItem() instanceof BrewfestBootsItem boots) {
            Model model = ArmorRegistry.getBootsModel(boots, contextModel.rightLeg, contextModel.leftLeg);
            model.renderToBuffer(poseStack,
                    buffer.getBuffer(model.renderType(boots.getBootsTexture())),
                    light,
                    OverlayTexture.NO_OVERLAY);
        }
    }
}
