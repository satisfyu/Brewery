package net.bmjo.brewery.event;

import dev.architectury.event.events.common.LootEvent;
import net.bmjo.brewery.util.BreweryLoottableInjector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTables;

public class CommonEvents {

    public static void init() {
        LootEvent.MODIFY_LOOT_TABLE.register(CommonEvents::onModifyLootTable);
    }

    public static void onModifyLootTable(LootTables tables, ResourceLocation id, LootEvent.LootTableModificationContext context, boolean builtin) {
        BreweryLoottableInjector.InjectLoot(id, context);
    }
}
