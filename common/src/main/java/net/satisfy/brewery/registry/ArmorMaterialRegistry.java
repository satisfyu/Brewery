package net.satisfy.brewery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.util.BreweryIdentifier;

import java.util.EnumMap;
import java.util.List;

public class ArmorMaterialRegistry {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Brewery.MOD_ID, Registries.ARMOR_MATERIAL);
    private static final Ingredient WOOL_REPAIR_INGREDIENT = Ingredient.of(ItemTags.WOOL);

    public static final Holder<ArmorMaterial> BREWFEST_ARMOR = ARMOR_MATERIALS.register("lederhosen", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 1);
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.BODY, 1);
            }),
            ArmorMaterials.LEATHER.value().enchantmentValue(),
            ArmorMaterials.LEATHER.value().equipSound(),
            () -> WOOL_REPAIR_INGREDIENT,
            List.of(
                    new ArmorMaterial.Layer(
                            BreweryIdentifier.of("brewfest")
                    )
            ),
            ArmorMaterials.LEATHER.value().toughness(),
            ArmorMaterials.LEATHER.value().knockbackResistance()
    ));

    public static final Holder<ArmorMaterial> BREWFEST_LEATHER = BREWFEST_ARMOR;
    public static final Holder<ArmorMaterial> BREWFEST_DRESS = ARMOR_MATERIALS.register("dirndl", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 1);
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.BODY, 1);
            }),
                    ArmorMaterials.IRON.value().enchantmentValue(),
                    ArmorMaterials.TURTLE.value().equipSound(),
                    () -> WOOL_REPAIR_INGREDIENT,
                    List.of(
                            new ArmorMaterial.Layer(
                                    BreweryIdentifier.of("dirndl")
                            )
                    ),
                    ArmorMaterials.LEATHER.value().toughness(),
                    ArmorMaterials.LEATHER.value().knockbackResistance()
            ));

    public static void init() {
        ARMOR_MATERIALS.register();
    }
}
