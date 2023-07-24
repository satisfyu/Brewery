package net.bmjo.brewery.effect;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.Supplier;

public class BreweryEffects {

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Brewery.MOD_ID, Registry.MOB_EFFECT_REGISTRY);
    private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();

    public static final RegistrySupplier<MobEffect> DRUNK;
    public static final RegistrySupplier<MobEffect> BLACKOUT;


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
        BLACKOUT = registerEffect("blackout", () -> new BlackoutEffect().setFactorDataFactory(() -> new MobEffectInstance.FactorData(22)));
    }
}
