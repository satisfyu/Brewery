package net.bmjo.brewery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.entity.BeerKegFlowerPotBlockEntity;
import net.bmjo.brewery.entity.BrewstationEntity;
import net.bmjo.brewery.entity.StandardBlockEntity;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    private static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Brewery.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY).getRegistrar();

    public static final RegistrySupplier<BlockEntityType<BrewstationEntity>> BREWINGSTATION_BLOCK_ENTITY = create("brewingstation", () -> BlockEntityType.Builder.of(BrewstationEntity::new, ObjectRegistry.WOODEN_BREWINGSTATION.get(),  ObjectRegistry.COPPER_BREWINGSTATION.get(),  ObjectRegistry.NETHERITE_BREWINGSTATION.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<BeerKegFlowerPotBlockEntity>> BEER_MUG_FLOWER_POT_BLOCK_ENTITY = create("beer_mug", () -> BlockEntityType.Builder.of(BeerKegFlowerPotBlockEntity::new, ObjectRegistry.BEER_MUG.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<StandardBlockEntity>> STANDARD = create("standard", () -> BlockEntityType.Builder.of(StandardBlockEntity::new, ObjectRegistry.STANDARD_BLOCKS.stream().map(Supplier::get).toList().toArray(new Block[0])).build(null));


    private static <T extends BlockEntityType<?>> RegistrySupplier<T> create(final String path, final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(new BreweryIdentifier(path), type);
    }

    public static void registerBlockEntities() {
        
    }
}
