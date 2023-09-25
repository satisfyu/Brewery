package net.bmjo.brewery.fabric.registry;

import net.bmjo.brewery.registry.*;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.bmjo.brewery.util.BreweryVillagerUtil;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;

public class BreweryFabricVillagers {

    private static final BreweryIdentifier BREWER_POI_IDENTIFIER = new BreweryIdentifier("brewer_poi");
    public static final PoiType BREWER_POI = PointOfInterestHelper.register(BREWER_POI_IDENTIFIER, 1, 12, ObjectRegistry.BAR_BLOCK.get());
    public static final VillagerProfession BREWER = Registry.register(Registry.VILLAGER_PROFESSION, new ResourceLocation("brewery", "brewer"), VillagerProfessionBuilder.create().id(new ResourceLocation("brewery", "brewer")).workstation(ResourceKey.create(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, BREWER_POI_IDENTIFIER)).build());

    public static void init() {
        TradeOfferHelper.registerVillagerOffers(BREWER, 1, factories -> {
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.HOPS_SEEDS.get(), 2, 3, 5));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.CORN_SEEDS.get(), 2, 3, 5));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BARLEY_SEEDS.get(), 2, 3, 5));
        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 2, factories -> {
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.HOPS.get(), 4, 2, 7));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.CORN.get(), 4, 2, 7));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BARLEY.get(), 4, 2, 7));

        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 3, factories -> {
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.PORK_KNUCKLE.get(), 7, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.PRETZEL.get(), 7, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.DUMPLINGS.get(), 7, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.SAUSAGE.get(), 7, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.GINGERBREAD.get(), 7, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.MASHED_POTATOES.get(), 7, 1, 10));

        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 4, factories -> {
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_TROUSERS.get(), 4, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_BOOTS.get(), 5, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_SHOES.get(), 5, 1, 10));

        });
        TradeOfferHelper.registerVillagerOffers(BREWER, 5, factories -> {
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_HAT.get(), 10, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_REGALIA.get(), 10, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_DRESS.get(), 10, 1, 10));
            factories.add(new BreweryVillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_BLOUSE.get(), 10, 1, 10));


        });

    }


}
