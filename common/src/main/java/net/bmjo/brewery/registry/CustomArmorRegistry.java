package net.bmjo.brewery.registry;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.bmjo.brewery.client.model.BrewfestHatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.Map;

public class CustomArmorRegistry {


    public static void registerCustomArmorLayers(){
        EntityModelLayerRegistry.register(BrewfestHatModel.LAYER_LOCATION, BrewfestHatModel::getTexturedModelData);
    }


    public static  <T extends LivingEntity> void registerHatModels(Map<Item, EntityModel<T>> models, EntityModelSet modelLoader) {
        models.put(ObjectRegistry.BREWFEST_HAT.get(), new BrewfestHatModel<>(modelLoader.bakeLayer(BrewfestHatModel.LAYER_LOCATION)));
    }
}
