package net.satisfy.brewery.core.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ArmorMaterialRegistry {

    public static final Holder<ArmorMaterial> BREWFEST = register("leather", (EnumMap) Util.make(new EnumMap(ArmorItem.Type.class), (enumMap) -> {
        enumMap.put(ArmorItem.Type.BOOTS, 1);
        enumMap.put(ArmorItem.Type.LEGGINGS, 2);
        enumMap.put(ArmorItem.Type.CHESTPLATE, 3);
        enumMap.put(ArmorItem.Type.HELMET, 1);
        enumMap.put(ArmorItem.Type.BODY, 3);
    }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return Ingredient.of(new ItemLike[]{Items.LEATHER});
    }, List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("brewfest"), "", true), new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "_overlay", false)));

    private static Holder<ArmorMaterial> register(String string, EnumMap<ArmorItem.Type, Integer> enumMap, int i, Holder<SoundEvent> holder, float f, float g, Supplier<Ingredient> supplier) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace(string)));
        return register(string, enumMap, i, holder, f, g, supplier, list);
    }

    private static Holder<ArmorMaterial> register(String string, EnumMap<ArmorItem.Type, Integer> enumMap, int i, Holder<SoundEvent> holder, float f, float g, Supplier<Ingredient> supplier, List<ArmorMaterial.Layer> list) {
        EnumMap<ArmorItem.Type, Integer> enumMap2 = new EnumMap(ArmorItem.Type.class);
        ArmorItem.Type[] var9 = ArmorItem.Type.values();
        int var10 = var9.length;

        for (ArmorItem.Type type : var9) {
            enumMap2.put(type, enumMap.get(type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.withDefaultNamespace(string), new ArmorMaterial(enumMap2, i, holder, supplier, list, f, g));
    }
}
