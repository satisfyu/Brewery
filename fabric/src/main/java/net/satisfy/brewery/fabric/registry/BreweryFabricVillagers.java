package net.satisfy.brewery.fabric.registry;

import de.cristelknight.doapi.common.util.VillagerUtil;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.satisfy.brewery.registry.ObjectRegistry;
import net.satisfy.brewery.util.BreweryIdentifier;

@SuppressWarnings("unused, deprecation")
public class BreweryFabricVillagers {

    private static final ResourceLocation BREWER_POI_IDENTIFIER = BreweryIdentifier.of("brewer_poi");
    public static final PoiType BREWER_POI = PointOfInterestHelper.register(BREWER_POI_IDENTIFIER, 1, 12, ObjectRegistry.BAR_COUNTER.get());
    public static final VillagerProfession BREWER = Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, ResourceLocation.fromNamespaceAndPath("brewery", "brewer"), VillagerProfessionBuilder.create().id(ResourceLocation.fromNamespaceAndPath("brewery", "brewer")).workstation(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, BREWER_POI_IDENTIFIER)).build());

    public static void init() {
        TradeOfferHelper.registerVillagerOffers(BREWER, 1, factories -> {
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.HOPS_SEEDS.get(), 2, 3, 5));
            factories.add(new VillagerUtil.SellItemFactory(net.satisfy.farm_and_charm.registry.ObjectRegistry.KERNELS.get(), 2, 3, 5));
            factories.add(new VillagerUtil.SellItemFactory(net.satisfy.farm_and_charm.registry.ObjectRegistry.BARLEY_SEEDS.get(), 2, 3, 5));
        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 2, factories -> {
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.HOPS.get(), 4, 2, 7));
            factories.add(new VillagerUtil.SellItemFactory(net.satisfy.farm_and_charm.registry.ObjectRegistry.CORN.get(), 4, 2, 7));
            factories.add(new VillagerUtil.SellItemFactory(net.satisfy.farm_and_charm.registry.ObjectRegistry.BARLEY.get(), 4, 2, 7));
            factories.add(new VillagerUtil.SellItemFactory(net.satisfy.farm_and_charm.registry.ObjectRegistry.OAT.get(), 4, 2, 7));
        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 3, factories -> {
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.PORK_KNUCKLE.get(), 7, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.PRETZEL.get(), 7, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.DUMPLINGS.get(), 7, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.SAUSAGE.get(), 7, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.GINGERBREAD.get(), 7, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.MASHED_POTATOES.get(), 7, 1, 10));

        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 4, factories -> {
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_TROUSERS.get(), 4, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_BOOTS.get(), 5, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_SHOES.get(), 5, 1, 10));

        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 5, factories -> {
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_HAT.get(), 10, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_HAT_RED.get(), 10, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_REGALIA.get(), 10, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_DRESS.get(), 10, 1, 10));
            factories.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_BLOUSE.get(), 10, 1, 10));
        });

    }


}
