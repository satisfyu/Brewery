package net.satisfy.brewery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.effect.*;

public class MobEffectRegistry {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Brewery.MOD_ID, Registries.MOB_EFFECT);

    public static final RegistrySupplier<MobEffect> DRUNK = MOB_EFFECTS.register("drunk", DrunkEffect::new);
    public static final RegistrySupplier<MobEffect> BLACKOUT = MOB_EFFECTS.register("blackout", () -> new BlackoutEffect().setBlendDuration(22));
    public static final RegistrySupplier<MobEffect> MINING = MOB_EFFECTS.register("mining", () -> new MiningEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> PACIFY = MOB_EFFECTS.register("pacify", () -> new PacifyEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> REPULSION = MOB_EFFECTS.register("repulsion", () -> new RepulsionEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> LIGHTNING_STRIKE = MOB_EFFECTS.register("lightning_strike", () -> new LightningStrikeEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> EXPLOSION = MOB_EFFECTS.register("explosion", () -> new ExplosionEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> COMBUSTION = MOB_EFFECTS.register("combustion", () -> new CombustionEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> TOXICTOUCH = MOB_EFFECTS.register("toxictouch", () -> new ToxicTouchEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> RENEWINGTOUCH = MOB_EFFECTS.register("renewingtouch", () -> new RenewingTouchEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> HEALINGTOUCH = MOB_EFFECTS.register("healingtouch", () -> new HealingTouchEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> PROTECTIVETOUCH = MOB_EFFECTS.register("protectivetouch", () -> new ProtectiveTouchEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> PARTYSTARTER = MOB_EFFECTS.register("partystarter", () -> new PartystarterEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> SNOWWHITE = MOB_EFFECTS.register("snowwhite", () -> new SnowWhiteEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> PINTCHARISMA = MOB_EFFECTS.register("pintcharisma", () -> new PintCharismaEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final RegistrySupplier<MobEffect> HALEY = MOB_EFFECTS.register("haley", () -> new HaleyEffect(MobEffectCategory.BENEFICIAL, 0));

    public static void init() {
        MOB_EFFECTS.register();
    }
}
