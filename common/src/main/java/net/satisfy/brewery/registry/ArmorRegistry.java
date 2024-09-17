package net.satisfy.brewery.registry;

import de.cristelknight.doapi.client.render.feature.CustomArmorManager;
import de.cristelknight.doapi.client.render.feature.CustomArmorSet;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.satisfy.brewery.client.model.*;
import net.satisfy.brewery.item.armor.IBrewfestArmorSet;
import net.satisfy.brewery.util.BreweryIdentifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorRegistry {
    private static final Map<Item, BrewfestHatModel<?>> models = new HashMap<>();

    public static void registerArmorModelLayers() {
        EntityModelLayerRegistry.register(LederhosenInner.LAYER_LOCATION, LederhosenInner::createBodyLayer);
        EntityModelLayerRegistry.register(LederhosenOuter.LAYER_LOCATION, LederhosenOuter::createBodyLayer);
        EntityModelLayerRegistry.register(DirndlInner.LAYER_LOCATION, DirndlInner::createBodyLayer);
        EntityModelLayerRegistry.register(DirndlOuter.LAYER_LOCATION, DirndlOuter::createBodyLayer);
    }

    public static Model getHatModel(Item item, ModelPart baseHead) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();

        BrewfestHatModel<?> brewfestHatModel = models.computeIfAbsent(ObjectRegistry.BREWFEST_HAT.get(), key ->
                new BrewfestHatModel<>(modelSet.bakeLayer(BrewfestHatModel.LAYER_LOCATION))
        );

        BrewfestHatModel<?> brewfestHatRedModel = models.computeIfAbsent(ObjectRegistry.BREWFEST_HAT_RED.get(), key ->
                new BrewfestHatModel<>(modelSet.bakeLayer(BrewfestHatModel.LAYER_LOCATION))
        );

        BrewfestHatModel<?> model = (item == ObjectRegistry.BREWFEST_HAT.get()) ? brewfestHatModel : brewfestHatRedModel;

        model.copyHead(baseHead);

        return model;
    }



    public static <T extends LivingEntity> void registerArmorModels(CustomArmorManager<T> armors, EntityModelSet modelLoader) {
        armors.addArmor(new CustomArmorSet<T>(ObjectRegistry.BREWFEST_REGALIA.get(), ObjectRegistry.BREWFEST_BOOTS.get(),
                ObjectRegistry.BREWFEST_TROUSERS.get())
                .setTexture(BreweryIdentifier.of("lederhosen"))
                .setOuterModel(new LederhosenOuter<>(modelLoader.bakeLayer(LederhosenOuter.LAYER_LOCATION)))
                .setInnerModel(new LederhosenInner<>(modelLoader.bakeLayer(LederhosenInner.LAYER_LOCATION))));
        armors.addArmor(new CustomArmorSet<T>(ObjectRegistry.BREWFEST_BLOUSE.get(), ObjectRegistry.BREWFEST_DRESS.get(),
                ObjectRegistry.BREWFEST_SHOES.get())
                .setTexture(BreweryIdentifier.of("dirndl"))
                .setOuterModel(new DirndlOuter<>(modelLoader.bakeLayer(DirndlOuter.LAYER_LOCATION)))
                .setInnerModel(new DirndlInner<>(modelLoader.bakeLayer(DirndlInner.LAYER_LOCATION))));
    }

    public static void appendTooltip(List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        boolean helmet = IBrewfestArmorSet.hasBrewfestHelmet(player);
        boolean breastplate = IBrewfestArmorSet.hasBrewfestBreastplate(player);
        boolean leggings = IBrewfestArmorSet.hasBrewfestLeggings(player);
        boolean boots = IBrewfestArmorSet.hasBrewfestBoots(player);
        tooltip.add(Component.nullToEmpty(""));
        tooltip.add(Component.translatable("tooltip.brewfest.brewfest_set").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(helmet ? Component.translatable("tooltip.brewfest.brewfesthelmet").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfesthelmet").withStyle(ChatFormatting.GRAY));
        tooltip.add(breastplate ? Component.translatable("tooltip.brewfest.brewfestbreastplate").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfestbreastplate").withStyle(ChatFormatting.GRAY));
        tooltip.add(leggings ? Component.translatable("tooltip.brewfest.brewfestleggings").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfestleggings").withStyle(ChatFormatting.GRAY));
        tooltip.add(boots ? Component.translatable("tooltip.brewfest.brewfestboots").withStyle(ChatFormatting.GREEN) : Component.translatable("tooltip.brewfest.brewfestboots").withStyle(ChatFormatting.GRAY));
    }
}
