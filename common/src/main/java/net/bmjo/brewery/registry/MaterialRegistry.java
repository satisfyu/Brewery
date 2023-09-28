package net.bmjo.brewery.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class MaterialRegistry {
    private static final Ingredient WOOL_REPAIR_INGREDIENT = Ingredient.of(ItemTags.WOOL);
    public static final ArmorMaterial BREWFEST_ARMOR = new ArmorMaterial() {
        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return ArmorMaterials.LEATHER.getDurabilityForSlot(slot);
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return ArmorMaterials.LEATHER.getDefenseForSlot(slot);
        }

        @Override
        public int getEnchantmentValue() {
            return ArmorMaterials.LEATHER.getEnchantmentValue();
        }

        @Override
        public SoundEvent getEquipSound() {
            return ArmorMaterials.LEATHER.getEquipSound();
        }

        public Ingredient getRepairIngredient() {
            return WOOL_REPAIR_INGREDIENT;
        }

        @Override
        public String getName() {
            return "brewfest";
        }

        @Override
        public float getToughness() {
            return ArmorMaterials.LEATHER.getToughness();
        }

        @Override
        public float getKnockbackResistance() {
            return ArmorMaterials.LEATHER.getKnockbackResistance();
        }
    };

    public static final ArmorMaterial BREWFEST_LEATHER = new ArmorMaterial() {
        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return ArmorMaterials.LEATHER.getDurabilityForSlot(slot);
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return ArmorMaterials.LEATHER.getDefenseForSlot(slot);
        }

        @Override
        public int getEnchantmentValue() {
            return ArmorMaterials.LEATHER.getEnchantmentValue();
        }

        @Override
        public SoundEvent getEquipSound() {
            return ArmorMaterials.LEATHER.getEquipSound();
        }

        public Ingredient getRepairIngredient() {
            return ArmorMaterials.LEATHER.getRepairIngredient();
        }

        @Override
        public String getName() {
            return "brewfest";
        }

        @Override
        public float getToughness() {
            return ArmorMaterials.LEATHER.getToughness();
        }

        @Override
        public float getKnockbackResistance() {
            return ArmorMaterials.LEATHER.getKnockbackResistance();
        }
    };

    public static final ArmorMaterial BREWFEST_DRESS = new ArmorMaterial() {
        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return ArmorMaterials.LEATHER.getDurabilityForSlot(slot);
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return ArmorMaterials.CHAIN.getDefenseForSlot(slot);
        }

        @Override
        public int getEnchantmentValue() {
            return ArmorMaterials.IRON.getEnchantmentValue();
        }

        @Override
        public SoundEvent getEquipSound() {
            return ArmorMaterials.TURTLE.getEquipSound();
        }

        public Ingredient getRepairIngredient() {
            return WOOL_REPAIR_INGREDIENT;
        }

        @Override
        public String getName() {
            return "dirndl";
        }

        @Override
        public float getToughness() {
            return ArmorMaterials.LEATHER.getToughness();
        }

        @Override
        public float getKnockbackResistance() {
            return ArmorMaterials.LEATHER.getKnockbackResistance();
        }
    };
}
