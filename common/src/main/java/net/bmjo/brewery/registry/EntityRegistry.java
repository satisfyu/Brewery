package net.bmjo.brewery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.entity.HangingRopeEntity;
import net.bmjo.brewery.entity.RopeCollisionEntity;
import net.bmjo.brewery.entity.RopeKnotEntity;
import net.bmjo.brewery.entity.beer_elemental.BeerElementalEntity;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class EntityRegistry {
    private static final Registrar<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Brewery.MOD_ID, Registry.ENTITY_TYPE_REGISTRY).getRegistrar();

    public static final RegistrySupplier<EntityType<RopeKnotEntity>> ROPE_KNOT = create("rope_knot",
            () -> EntityType.Builder.of(RopeKnotEntity::new, MobCategory.MISC)
                    .sized(6 / 16F, 4 / 16F)
                    .clientTrackingRange(20)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .build(new BreweryIdentifier("rope_knot").toString())
    );

    public static final RegistrySupplier<EntityType<RopeCollisionEntity>> ROPE_COLLISION = create("rope_collision", () -> EntityType.Builder.of(RopeCollisionEntity::new, MobCategory.MISC)
                    .sized(4 / 16f, 4 / 16f)
                    .clientTrackingRange(10)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .build(new BreweryIdentifier("rope_collision").toString())
    );

    public static final RegistrySupplier<EntityType<HangingRopeEntity>> HANGING_ROPE = create("hanging_rope",
            () -> EntityType.Builder.of(HangingRopeEntity::new, MobCategory.MISC)
                    .sized(4 / 16f, 4 / 16f)
                    .clientTrackingRange(10)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .build(new BreweryIdentifier("hanging_rope").toString())
    );

    public static final RegistrySupplier<EntityType<BeerElementalEntity>> BEER_ELEMENTAL = create("beer_elemental",
            () -> EntityType.Builder.of(BeerElementalEntity::new, MobCategory.MONSTER)
                    .sized(1F, 1F)
                    .clientTrackingRange(80)
                    .updateInterval(3)
                    .fireImmune()
                    .build(new BreweryIdentifier("beer_elemental").toString())
    );

    public static <T extends EntityType<?>> RegistrySupplier<T> create(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(new BreweryIdentifier(path), type);
    }

    public static void register() {

    }
}
