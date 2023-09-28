package net.bmjo.brewery.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.effect.*;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.Supplier;

public class EffectRegistry {

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Brewery.MOD_ID, Registry.MOB_EFFECT_REGISTRY);
    private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();

    public static final RegistrySupplier<MobEffect> DRUNK;
    public static final RegistrySupplier<MobEffect> BLACKOUT;
    public static final RegistrySupplier<MobEffect> TELEPORT;
    public static final RegistrySupplier<MobEffect> SATURATED;
    public static final RegistrySupplier<MobEffect> CUDDLYWARM;
    public static final RegistrySupplier<MobEffect> HEARTHSTONE;
    public static final RegistrySupplier<MobEffect> SLIDING;
    public static final RegistrySupplier<MobEffect> GRAVEDIGGER;
    public static final RegistrySupplier<MobEffect> DOUBLEJUMP;
    public static final RegistrySupplier<MobEffect> HARDDRINKING;


    private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect){
        if(Platform.isForge()){
            return MOB_EFFECTS.register(name, effect);
        }
        return MOB_EFFECTS_REGISTRAR.register(new BreweryIdentifier(name), effect);
    }

    public static void registerEffects(){
        Brewery.LOGGER.debug("Mob effects");
        MOB_EFFECTS.register();
    }

    static {
        DRUNK = registerEffect("drunk", DrunkEffect::new);
        HARDDRINKING = registerEffect("harddrinking", HarddrinkingEffect::new);
        BLACKOUT = registerEffect("blackout", () -> new BlackoutEffect().setFactorDataFactory(() -> new MobEffectInstance.FactorData(22)));
        TELEPORT = registerEffect("teleport", TeleportEffect::new);
        SATURATED = registerEffect("saturated", SaturatedEffect::new);
        CUDDLYWARM = registerEffect("cuddlywarm", CuddlyWarmEffect::new);
        HEARTHSTONE = registerEffect("hearthstone", HearthstoneEffect::new);
        SLIDING = registerEffect("sliding", SlidingEffect::new);
        GRAVEDIGGER = registerEffect("gravedigger", GravediggerEffect::new);
        DOUBLEJUMP = registerEffect("doublejump", () -> new DoubleJumpEffect(MobEffectCategory.BENEFICIAL, 0x90F891));

    }
}
