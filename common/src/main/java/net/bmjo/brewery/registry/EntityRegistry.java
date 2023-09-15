package net.bmjo.brewery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.entity.RopeCollisionEntity;
import net.bmjo.brewery.entity.RopeKnotEntity;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class EntityRegistry {
    private static final Registrar<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Brewery.MOD_ID, Registry.ENTITY_TYPE_REGISTRY).getRegistrar();

    public static final RegistrySupplier<EntityType<RopeKnotEntity>> HOP_ROPE_KNOT = create("hop_rope_knot",
            () -> EntityType.Builder.of(RopeKnotEntity::new, MobCategory.MISC)
                    .sized(6 / 16F, 0.5F)
                    .clientTrackingRange(20)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .build(new BreweryIdentifier("hop_rope_knot").toString())
    );

    public static final RegistrySupplier<EntityType<RopeCollisionEntity>> ROPE_COLLISION = create("rope_collision",
            () -> EntityType.Builder.of(RopeCollisionEntity::new, MobCategory.MISC)
                    .sized(4 / 16f, 6 / 16f)
                    .clientTrackingRange(10)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .build(new BreweryIdentifier("rope_collision").toString())
    );

    public static <T extends EntityType<?>> RegistrySupplier<T> create(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(new BreweryIdentifier(path), type);
    }

    public static void register() {

    }
}
