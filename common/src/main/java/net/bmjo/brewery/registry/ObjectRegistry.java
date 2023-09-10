package net.bmjo.brewery.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.block.Oven;
import net.bmjo.brewery.block.SteamWhistle;
import net.bmjo.brewery.block.Timer;
import net.bmjo.brewery.block.WaterBasin;
import net.bmjo.brewery.item.Breathalyzer;
import net.bmjo.brewery.item.HopRope;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ObjectRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Brewery.MOD_ID,  Registry.ITEM_REGISTRY);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Brewery.MOD_ID, Registry.BLOCK_REGISTRY);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();
    public static final CreativeModeTab VINERY_TAB = CreativeTabRegistry.create(new BreweryIdentifier("brewery"), () ->
            new ItemStack(ObjectRegistry.BREATHALYZER.get()));

    public static final RegistrySupplier<Item> BREATHALYZER = registerI("breathalyzer", () -> new Breathalyzer(getSettings()));
    public static final RegistrySupplier<Item> HOP_ROPE = registerI("hop_rope", () -> new HopRope(getSettings()));
    public static final RegistrySupplier<Block> WATER_BASIN = registerBI("water_basin", () -> new WaterBasin(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistrySupplier<Block> STEAM_WHISTLE = registerBI("steam_whistle", () -> new SteamWhistle(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistrySupplier<Block> OVEN = registerBI("oven", () -> new Oven(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistrySupplier<Block> TIMER = registerBI("timer", () -> new Timer(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));


    private static <T extends Item> RegistrySupplier<T> registerI(String path, Supplier<T> item) {
        final ResourceLocation id = new BreweryIdentifier(path);
        return ITEM_REGISTRAR.register(id, item);
    }

    private static <T extends Block> RegistrySupplier<T> registerB(String path, Supplier<T> block) {
        final ResourceLocation id = new BreweryIdentifier(path);
        return BLOCK_REGISTRAR.register(id, block);
    }

    private static <T extends Block> RegistrySupplier<T> registerBI(String path, Supplier<T> block) {
        RegistrySupplier<T> blockSupplier = registerB(path, block);
        registerI(path, () -> new BlockItem(blockSupplier.get(), getSettings()));
        return blockSupplier;
    }

    private static Item.Properties getSettings() {
        return getSettings(settings -> {});
    }

    private static Item.Properties getSettings(Consumer<Item.Properties> consumer) {
        Item.Properties settings = new Item.Properties().tab(VINERY_TAB);
        consumer.accept(settings);
        return settings;
    }

    public static void register() {
        ITEMS.register();
        BLOCKS.register();
    }
}
