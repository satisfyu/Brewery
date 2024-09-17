package net.satisfy.brewery.util;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.satisfy.brewery.Brewery;

public class BreweryLoottableInjector {
    public static void InjectLoot(ResourceLocation id, LootEvent.LootTableModificationContext context) {
        String prefix = "minecraft:chests/";
        String name = id.toString();

        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            switch (file) {
                case "village/village_taiga_house", "woodland_mansion", "pillager_outpost",
                        "village/village_desert_house", "village/village_plains_house", "village/village_savanna_house" ->
                        context.addPool(getPool(file));
                default -> {
                }
            }
        }
    }

    public static LootPool.Builder getPool(String entryName)
    {
        return LootPool.lootPool().add(getPoolEntry(entryName));
    }

    @SuppressWarnings("rawtypes")
    private static LootPoolEntryContainer.Builder getPoolEntry(String name)
    {
        ResourceLocation table = ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, "chests/" + name);
        ResourceKey.create(Registries.LOOT_TABLE, table);

        return NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, table));
    }
}

