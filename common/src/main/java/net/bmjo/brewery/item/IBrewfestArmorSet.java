package net.bmjo.brewery.item;

import com.google.common.collect.ImmutableMap;
import net.bmjo.brewery.registry.EffectRegistry;
import net.bmjo.brewery.registry.MaterialRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Objects;

public interface IBrewfestArmorSet {

    Map<ArmorMaterial, MobEffectInstance> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>())
                    .put(MaterialRegistry.BREWFEST_ARMOR, new MobEffectInstance((MobEffect) EffectRegistry.HARDDRINKING, 14 * 20, 1)).build();

    default boolean hasBrewfestSet(Player player) {
        return hasBrewfestBoots(player) && hasBrewfestLeggings(player) && hasBrewfestBreastplate(player) && hasBrewfestHelmet(player);
    }

    default void checkForSet(Player player) {
        if (hasBrewfestSet(player)) {
            addStatusEffectForMaterial(player, new MobEffectInstance((MobEffect) EffectRegistry.HARDDRINKING, 14 * 20, 2));
        }
        hasBrewfest(player);
    }

    default void hasBrewfest(Player player) {
        for (Map.Entry<ArmorMaterial, MobEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            ArmorMaterial mapArmorMaterial = entry.getKey();
            MobEffectInstance mapStatusEffect = entry.getValue();

            if (hasCorrectBrewfest(mapArmorMaterial, player)) {
                addStatusEffectForMaterial(player, mapStatusEffect);
            }
        }
    }

    private boolean hasCorrectBrewfest(ArmorMaterial material, Player player) {
        if (material.equals(MaterialRegistry.BREWFEST_ARMOR)) {
            int slot = 1;
            if (!player.getInventory().getArmor(slot).isEmpty()) {
                ArmorItem armor = (ArmorItem) player.getInventory().getArmor(slot).getItem();
                return armor.getMaterial() == material;
            }
            return false;
        }
        return false;
    }


    default void addStatusEffectForMaterial(Player player, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());

        if (!hasPlayerEffect || Objects.requireNonNull(player.getEffect(mapStatusEffect.getEffect())).getDuration() < 11 * 20) {
            player.addEffect(new MobEffectInstance(mapStatusEffect.getEffect(),
                    mapStatusEffect.getDuration(), mapStatusEffect.getAmplifier(), true, false, true));
        }
    }

    static boolean hasBrewfestBoots(Player player) {
        if (player.getInventory().getArmor(0).isEmpty()) return false;
        Item item = player.getInventory().getArmor(0).getItem();
        if (item instanceof ArmorItem armorItem) {
            return isBrewfestBoots(armorItem);
        }
        return false;
    }

    private static boolean isBrewfestBoots(ArmorItem armorItem) {
        return armorItem.getMaterial() == MaterialRegistry.BREWFEST_ARMOR;
    }

    static boolean hasBrewfestLeggings(Player player) {
        if (player.getInventory().getArmor(1).isEmpty()) return false;
        Item item = player.getInventory().getArmor(1).getItem();
        if (item instanceof ArmorItem armorItem) {
            return isBrewfestLeggings(armorItem);
        }
        return false;
    }

    private static boolean isBrewfestLeggings(ArmorItem armorItem) {
        return armorItem.getMaterial() == MaterialRegistry.BREWFEST_LEATHER;
    }

    static boolean hasBrewfestBreastplate(Player player) {
        if (player.getInventory().getArmor(2).isEmpty()) return false;
        Item item = player.getInventory().getArmor(2).getItem();
        if (item instanceof ArmorItem armorItem) {
            return isBrewfestBreastplate(armorItem);
        }
        return false;
    }

    private static boolean isBrewfestBreastplate(ArmorItem armorItem) {
        return armorItem.getMaterial() == MaterialRegistry.BREWFEST_ARMOR;
    }

    static boolean hasBrewfestHelmet(Player player) {
        if (player.getInventory().getArmor(3).isEmpty()) return false;
        Item item = player.getInventory().getArmor(3).getItem();
        if (item instanceof ArmorItem armorItem) {
            return isBrewfestHelmet(armorItem);
        }
        return false;
    }


    private static boolean isBrewfestHelmet(ArmorItem armorItem) {
        return armorItem.getMaterial() == MaterialRegistry.BREWFEST_LEATHER;
    }
}
