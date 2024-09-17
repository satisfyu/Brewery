package net.satisfy.brewery.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.effect.*;
import net.satisfy.brewery.util.BreweryIdentifier;

import java.util.function.Supplier;

public class MobEffectRegistry {

    public static final RegistrySupplier<MobEffect> DRUNK;
    public static final RegistrySupplier<MobEffect> BLACKOUT;
    public static final RegistrySupplier<MobEffect> RENEWINGTOUCH;
    public static final RegistrySupplier<MobEffect> TOXICTOUCH;
    public static final RegistrySupplier<MobEffect> HEALINGTOUCH;
    public static final RegistrySupplier<MobEffect> PROTECTIVETOUCH;
    public static final RegistrySupplier<MobEffect> PARTYSTARTER;
    public static final RegistrySupplier<MobEffect> SNOWWHITE;
    public static final RegistrySupplier<MobEffect> PINTCHARISMA;
    public static final RegistrySupplier<MobEffect> HALEY;
    public static final RegistrySupplier<MobEffect> MINING;
    public static final RegistrySupplier<MobEffect> PACIFY;
    public static final RegistrySupplier<MobEffect> COMBUSTION;
    public static final RegistrySupplier<MobEffect> EXPLOSION;
    public static final RegistrySupplier<MobEffect> REPULSION;
    public static final RegistrySupplier<MobEffect> LIGHTNING_STRIKE;
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Brewery.MOD_ID, Registries.MOB_EFFECT);
    private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();

    static {
        DRUNK = registerEffect("drunk", DrunkEffect::new);
        BLACKOUT = registerEffect("blackout", BlackoutEffect::new);
        MINING = registerEffect("mining", () -> new MiningEffect(MobEffectCategory.BENEFICIAL, 0));
        PACIFY = registerEffect("pacify", () -> new PacifyEffect(MobEffectCategory.BENEFICIAL, 0));
        REPULSION = registerEffect("repulsion", () -> new RepulsionEffect(MobEffectCategory.BENEFICIAL, 0));
        LIGHTNING_STRIKE = registerEffect("lightning_strike", () -> new LightningStrikeEffect(MobEffectCategory.BENEFICIAL, 0));
        EXPLOSION = registerEffect("explosion", () -> new ExplosionEffect(MobEffectCategory.BENEFICIAL, 0));
        COMBUSTION = registerEffect("combustion", () -> new CombustionEffect(MobEffectCategory.BENEFICIAL, 0));
        TOXICTOUCH = registerEffect("toxictouch", () -> new ToxicTouchEffect(MobEffectCategory.BENEFICIAL, 0));
        RENEWINGTOUCH = registerEffect("renewingtouch", () -> new RenewingTouchEffect(MobEffectCategory.BENEFICIAL, 0));
        HEALINGTOUCH = registerEffect("healingtouch", () -> new HealingTouchEffect(MobEffectCategory.BENEFICIAL, 0));
        PROTECTIVETOUCH = registerEffect("protectivetouch", () -> new ProtectiveTouchEffect(MobEffectCategory.BENEFICIAL, 0));
        PARTYSTARTER = registerEffect("partystarter", () -> new PartystarterEffect(MobEffectCategory.BENEFICIAL, 0));
        SNOWWHITE = registerEffect("snowwhite", () -> new SnowWhiteEffect(MobEffectCategory.BENEFICIAL, 0));
        PINTCHARISMA = registerEffect("pintcharisma", () -> new PintCharismaEffect(MobEffectCategory.BENEFICIAL, 0));
        HALEY = registerEffect("haley", () -> new HaleyEffect(MobEffectCategory.BENEFICIAL, 0));
    }

    private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
        if (Platform.isNeoForge()) {
            return MOB_EFFECTS.register(name, effect);
        }
        return MOB_EFFECTS_REGISTRAR.register(BreweryIdentifier.of(name), effect);
    }

    public static void init() {
        Brewery.LOGGER.debug("Mob effects");
        MOB_EFFECTS.register();
    }
}
