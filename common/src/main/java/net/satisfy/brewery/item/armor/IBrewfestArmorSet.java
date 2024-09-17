package net.satisfy.brewery.item.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.satisfy.brewery.registry.ArmorMaterialRegistry;

public interface IBrewfestArmorSet {
    static boolean hasBrewfestBoots(Player player) {
        if (player.getInventory().getArmor(0).isEmpty()) return false;
        Item item = player.getInventory().getArmor(0).getItem();
        if (item instanceof ArmorItem armorItem) {
            return isBrewfestBoots(armorItem);
        }
        return false;
    }

    private static boolean isBrewfestBoots(ArmorItem armorItem) {
        return armorItem == ArmorMaterialRegistry.BREWFEST_ARMOR || armorItem == ArmorMaterialRegistry.BREWFEST_DRESS || armorItem == ArmorMaterialRegistry.BREWFEST_LEATHER;
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
        return armorItem == ArmorMaterialRegistry.BREWFEST_LEATHER || armorItem == ArmorMaterialRegistry.BREWFEST_DRESS || armorItem == ArmorMaterialRegistry.BREWFEST_ARMOR;
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
        return armorItem == ArmorMaterialRegistry.BREWFEST_ARMOR || armorItem == ArmorMaterialRegistry.BREWFEST_DRESS || armorItem == ArmorMaterialRegistry.BREWFEST_LEATHER;
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
        return armorItem == ArmorMaterialRegistry.BREWFEST_LEATHER || armorItem == ArmorMaterialRegistry.BREWFEST_DRESS;
    }
}
