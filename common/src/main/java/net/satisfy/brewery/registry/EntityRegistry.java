package net.satisfy.brewery.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.entity.BeerElementalAttackEntity;
import net.satisfy.brewery.entity.BeerElementalEntity;
import net.satisfy.brewery.entity.DarkBrewEntity;
import net.satisfy.brewery.block.entity.rope.HangingRopeEntity;
import net.satisfy.brewery.block.entity.rope.RopeCollisionEntity;
import net.satisfy.brewery.block.entity.rope.RopeKnotEntity;
import net.satisfy.brewery.util.BreweryIdentifier;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class EntityRegistry {
    private static final Registrar<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Brewery.MOD_ID, Registries.ENTITY_TYPE).getRegistrar();

    public static final RegistrySupplier<EntityType<DarkBrewEntity>> DARK_BREW = create("dark_brew",
            () -> EntityType.Builder.<DarkBrewEntity>of(DarkBrewEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(BreweryIdentifier.of("dark_brew").toString())
    );

    public static final RegistrySupplier<EntityType<RopeKnotEntity>> ROPE_KNOT = create("rope_knot",
            () -> EntityType.Builder.of(RopeKnotEntity::new, MobCategory.MISC)
                    .sized(6 / 16F, 4 / 16F)
                    .clientTrackingRange(20)
                    .canSpawnFarFromPlayer()
                    .fireImmune()
                    .build(BreweryIdentifier.of("rope_knot").toString())
    );

    public static final RegistrySupplier<EntityType<RopeCollisionEntity>> ROPE_COLLISION = create("rope_collision", ()
            -> EntityType.Builder.of(RopeCollisionEntity::new, MobCategory.MISC)
            .sized(4 / 16f, 4 / 16f)
            .clientTrackingRange(10)
            .noSave()
            .noSummon()
            .fireImmune()
            .build(BreweryIdentifier.of("rope_collision").toString())
    );

    public static final RegistrySupplier<EntityType<HangingRopeEntity>> HANGING_ROPE = create("hanging_rope",
            () -> EntityType.Builder.of(HangingRopeEntity::new, MobCategory.MISC)
                    .sized(4 / 16f, 4 / 16f)
                    .clientTrackingRange(10)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .build(BreweryIdentifier.of("hanging_rope").toString())
    );

    public static final RegistrySupplier<EntityType<BeerElementalEntity>> BEER_ELEMENTAL = create("beer_elemental",
            () -> EntityType.Builder.of(BeerElementalEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.6F)
                    .clientTrackingRange(80)
                    .updateInterval(3)
                    .build(BreweryIdentifier.of("beer_elemental").toString())
    );

    public static final RegistrySupplier<EntityType<BeerElementalAttackEntity>> BEER_ELEMENTAL_ATTACK = create("beer_elemental_attack",
            () -> EntityType.Builder.<BeerElementalAttackEntity>of(BeerElementalAttackEntity::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(BreweryIdentifier.of("beer_elemental_attack").toString())
    );

    public static <T extends EntityType<?>> RegistrySupplier<T> create(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(BreweryIdentifier.of(path), type);
    }

    public static void registerBeerElemental() {
        EntityAttributeRegistry.register(BEER_ELEMENTAL, BeerElementalEntity::createAttributes);
    }

    public static void init() {
        registerBeerElemental();
    }
}

