package net.satisfy.brewery.neoforge;

import de.cristelknight.doapi.common.util.VillagerUtil;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.neoforge.registry.BreweryForgeVillagers;
import net.satisfy.brewery.registry.ObjectRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.satisfy.farm_and_charm.registry.ObjectRegistry.*;

public class BreweryEvents {

    @EventBusSubscriber(modid = Brewery.MOD_ID)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void addCustomTrades(VillagerTradesEvent event) {
            if (event.getType().equals(BreweryForgeVillagers.BREWER.get())) {
                Map<Integer, List<VillagerTrades.ItemListing>> trades = event.getTrades();

                List<VillagerTrades.ItemListing> level1 = trades.computeIfAbsent(1, k -> new ArrayList<>());
                level1.add(new VillagerUtil.SellItemFactory(ObjectRegistry.HOPS_SEEDS.get(), 2, 3, 5));
                level1.add(new VillagerUtil.SellItemFactory(KERNELS.get(), 2, 3, 5));
                level1.add(new VillagerUtil.SellItemFactory(BARLEY_SEEDS.get(), 2, 3, 5));

                List<VillagerTrades.ItemListing> level2 = trades.computeIfAbsent(2, k -> new ArrayList<>());
                level2.add(new VillagerUtil.SellItemFactory(ObjectRegistry.HOPS.get(), 4, 2, 7));
                level2.add(new VillagerUtil.SellItemFactory(CORN.get(), 4, 2, 7));
                level2.add(new VillagerUtil.SellItemFactory(BARLEY.get(), 4, 2, 7));

                List<VillagerTrades.ItemListing> level3 = trades.computeIfAbsent(3, k -> new ArrayList<>());
                level3.add(new VillagerUtil.SellItemFactory(ObjectRegistry.PORK_KNUCKLE.get(), 7, 1, 10));
                level3.add(new VillagerUtil.SellItemFactory(ObjectRegistry.PRETZEL.get(), 7, 1, 10));
                level3.add(new VillagerUtil.SellItemFactory(ObjectRegistry.SAUSAGE.get(), 7, 1, 10));
                level3.add(new VillagerUtil.SellItemFactory(ObjectRegistry.DUMPLINGS.get(), 7, 1, 10));
                level3.add(new VillagerUtil.SellItemFactory(ObjectRegistry.GINGERBREAD.get(), 7, 1, 10));
                level3.add(new VillagerUtil.SellItemFactory(ObjectRegistry.MASHED_POTATOES.get(), 7, 1, 10));

                List<VillagerTrades.ItemListing> level4 = trades.computeIfAbsent(4, k -> new ArrayList<>());
                level4.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_TROUSERS.get(), 4, 1, 10));
                level4.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_BOOTS.get(), 5, 1, 10));
                level4.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_SHOES.get(), 5, 1, 10));

                List<VillagerTrades.ItemListing> level5 = trades.computeIfAbsent(5, k -> new ArrayList<>());
                level5.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_HAT.get(), 10, 1, 10));
                level5.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_HAT_RED.get(), 10, 1, 10));
                level5.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_REGALIA.get(), 10, 1, 10));
                level5.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_DRESS.get(), 10, 1, 10));
                level5.add(new VillagerUtil.SellItemFactory(ObjectRegistry.BREWFEST_BLOUSE.get(), 10, 1, 10));

            }
        }
    }
}
