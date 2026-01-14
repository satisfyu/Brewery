package net.satisfy.brewery.core.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.effect.*;

public class MobEffectRegistry {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Brewery.MOD_ID, Registries.MOB_EFFECT);
    private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();

    public static final RegistrySupplier<MobEffect> DRUNK;
    public static final RegistrySupplier<MobEffect> BLACKOUT;
    public static final RegistrySupplier<MobEffect> MINING;
    public static final RegistrySupplier<MobEffect> PACIFY;
    public static final RegistrySupplier<MobEffect> REPULSION;
    public static final RegistrySupplier<MobEffect> LIGHTNING_STRIKE;
    public static final RegistrySupplier<MobEffect> EXPLOSION;
    public static final RegistrySupplier<MobEffect> COMBUSTION;
    public static final RegistrySupplier<MobEffect> TOXICTOUCH;
    public static final RegistrySupplier<MobEffect> RENEWINGTOUCH;
    public static final RegistrySupplier<MobEffect> HEALINGTOUCH;
    public static final RegistrySupplier<MobEffect> PROTECTIVETOUCH;
    public static final RegistrySupplier<MobEffect> PARTYSTARTER;
    public static final RegistrySupplier<MobEffect> SNOWWHITE;
    public static final RegistrySupplier<MobEffect> PINTCHARISMA;
    public static final RegistrySupplier<MobEffect> HALEY;
    public static final RegistrySupplier<MobEffect> STOUTHEART;

    private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
        if (Platform.isNeoForge()) {
            return MOB_EFFECTS.register(name, effect);
        }
        return MOB_EFFECTS_REGISTRAR.register(Brewery.identifier(name), effect);
    }

    public static void init() {
        MOB_EFFECTS.register();
    }

    public static Holder<MobEffect> holder(RegistrySupplier<MobEffect> supplier) {
        return BuiltInRegistries.MOB_EFFECT.getResourceKey(supplier.get()).flatMap(BuiltInRegistries.MOB_EFFECT::getHolder).orElseThrow();
    }

    static {
        DRUNK = registerEffect("drunk", IntoxicationEffect::new);
        STOUTHEART = registerEffect("stoutheart", StoutHeartEffect::new);
        BLACKOUT = registerEffect("blackout", () -> new BlackoutEffect().setBlendDuration(22));
        MINING = registerEffect("mining", () -> new MiningEffect(MobEffectCategory.BENEFICIAL, 0x6B4F2A));
        PACIFY = registerEffect("pacify", () -> new PacifyEffect(MobEffectCategory.BENEFICIAL, 0x88DDEE));
        REPULSION = registerEffect("repulsion", () -> new RepulsionEffect(MobEffectCategory.BENEFICIAL, 0xFF4444));
        LIGHTNING_STRIKE = registerEffect("lightning_strike", () -> new LightningStrikeEffect(MobEffectCategory.BENEFICIAL, 0xE6E600));
        EXPLOSION = registerEffect("explosion", () -> new ExplosionEffect(MobEffectCategory.BENEFICIAL, 0xFF7700));
        COMBUSTION = registerEffect("combustion", () -> new CombustionEffect(MobEffectCategory.BENEFICIAL, 0xCC2200));
        TOXICTOUCH = registerEffect("toxictouch", () -> new ToxicTouchEffect(MobEffectCategory.HARMFUL, 0x00AA44));
        RENEWINGTOUCH = registerEffect("renewingtouch", () -> new RenewingTouchEffect(MobEffectCategory.BENEFICIAL, 0x66FF99));
        HEALINGTOUCH = registerEffect("healingtouch", () -> new HealingTouchEffect(MobEffectCategory.BENEFICIAL, 0xFF66CC));
        PROTECTIVETOUCH = registerEffect("protectivetouch", () -> new ProtectiveTouchEffect(MobEffectCategory.BENEFICIAL, 0x3399FF));
        PARTYSTARTER = registerEffect("partystarter", () -> new PartystarterEffect(MobEffectCategory.BENEFICIAL, 0xFF33AA));
        SNOWWHITE = registerEffect("snowwhite", () -> new SnowWhiteEffect(MobEffectCategory.BENEFICIAL, 0xE0FFFF));
        PINTCHARISMA = registerEffect("pintcharisma", () -> new PintCharismaEffect(MobEffectCategory.BENEFICIAL, 0xFFD700));
        HALEY = registerEffect("haley", () -> new HaleyEffect(MobEffectCategory.BENEFICIAL, 0xFF99FF));
    }
}
