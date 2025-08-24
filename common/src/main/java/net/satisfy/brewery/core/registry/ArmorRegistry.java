package net.satisfy.brewery.core.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.brewery.client.model.BrewfestBootsModel;
import net.satisfy.brewery.client.model.BrewfestChestplateModel;
import net.satisfy.brewery.client.model.BrewfestHatModel;
import net.satisfy.brewery.client.model.BrewfestLeggingsModel;
import net.satisfy.brewery.core.item.BrewfestBootsItem;
import net.satisfy.brewery.core.item.BrewfestChestItem;
import net.satisfy.brewery.core.item.BrewfestHatItem;
import net.satisfy.brewery.core.item.BrewfestLegsItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ArmorRegistry {
    private static final Map<Item, BrewfestHatModel<?>> models = new HashMap<>();
    private static final Map<Item, BrewfestChestplateModel<?>> chestplateModels = new HashMap<>();
    private static final Map<Item, BrewfestLeggingsModel<?>> leggingsModels = new HashMap<>();
    private static final Map<Item, BrewfestBootsModel<?>> bootsModels = new HashMap<>();

    public static Model getHatModel(Item item, ModelPart baseHead) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        BrewfestHatModel<?> model = models.computeIfAbsent(item, key -> {
            if (key == ObjectRegistry.BREWFEST_HAT_RED.get() || key == ObjectRegistry.BREWFEST_HAT.get()) {
                return new BrewfestHatModel<>(modelSet.bakeLayer(BrewfestHatModel.LAYER_LOCATION));
            } else {
                return null;
            }
        });

        if (model != null) {
            model.copyHead(baseHead);
        }

        return model;
    }

    public static Model getChestplateModel(Item item, ModelPart body, ModelPart leftArm, ModelPart rightArm, ModelPart leftLeg, ModelPart rightLeg) {
        BrewfestChestplateModel<?> model = chestplateModels.computeIfAbsent(item, key -> {
            if (key == ObjectRegistry.BREWFEST_BLOUSE.get() || key == ObjectRegistry.BREWFEST_REGALIA.get()) {
                return new BrewfestChestplateModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(BrewfestChestplateModel.LAYER_LOCATION));
            } else {
                return null;
            }
        });

        if (model != null) {
            model.copyBody(body, leftArm, rightArm, leftLeg, rightLeg);
        }

        return model;
    }

    public static Model getLeggingsModel(Item item, ModelPart rightLeg, ModelPart leftLeg) {
        BrewfestLeggingsModel<?> model = leggingsModels.computeIfAbsent(item, key -> {
            if (key == ObjectRegistry.BREWFEST_DRESS.get() || key == ObjectRegistry.BREWFEST_TROUSERS.get()) {
                return new BrewfestLeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(BrewfestLeggingsModel.LAYER_LOCATION));
            } else {
                return null;
            }
        });

        if (model != null) {
            model.copyLegs(rightLeg, leftLeg);
        }

        return model;
    }

    public static Model getBootsModel(Item item, ModelPart rightLeg, ModelPart leftLeg) {
        BrewfestBootsModel<?> model = bootsModels.computeIfAbsent(item, key -> {
            if (key == ObjectRegistry.BREWFEST_BOOTS.get() || key == ObjectRegistry.BREWFEST_SHOES.get()) {
                return new BrewfestBootsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(BrewfestBootsModel.LAYER_LOCATION));
            } else {
                return null;
            }
        });

        if (model != null) {
            model.copyLegs(rightLeg, leftLeg);
        }

        return model;
    }

    @SuppressWarnings("unused")
    public static void appendToolTip(@NotNull List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.getItem() instanceof BrewfestHatItem &&
                chestplate.getItem() instanceof BrewfestChestItem &&
                leggings.getItem() instanceof BrewfestLegsItem &&
                boots.getItem() instanceof BrewfestBootsItem;

        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("tooltip.brewery.armor.brewfest_set").withStyle(ChatFormatting.YELLOW));

        tooltip.add(createArmorTooltipEntry(helmet, BrewfestHatItem.class, "tooltip.brewery.armor.brewfesthelmet"));
        tooltip.add(createArmorTooltipEntry(chestplate, BrewfestChestItem.class, "tooltip.brewery.armor.brewfestbreastplate"));
        tooltip.add(createArmorTooltipEntry(leggings, BrewfestLegsItem.class, "tooltip.brewery.armor.brewfestleggings"));
        tooltip.add(createArmorTooltipEntry(boots, BrewfestBootsItem.class, "tooltip.brewery.armor.brewfestboots"));

        tooltip.add(Component.literal(""));

        ChatFormatting color = hasFullSet ? ChatFormatting.GREEN : ChatFormatting.GRAY;
    }

    private static Component createArmorTooltipEntry(ItemStack itemStack, Class<?> itemClass, String translationKey) {
        boolean isWorn = itemClass.isInstance(itemStack.getItem());
        ChatFormatting color = isWorn ? ChatFormatting.GREEN : ChatFormatting.GRAY;
        return Component.literal("- ")
                .append(Component.translatable(translationKey).withStyle(color));
    }

}
