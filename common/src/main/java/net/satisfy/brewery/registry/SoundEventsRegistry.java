package net.satisfy.brewery.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.util.BreweryIdentifier;

public class SoundEventsRegistry {

    private static final Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Brewery.MOD_ID, Registry.SOUND_EVENT_REGISTRY).getRegistrar();

    public static final RegistrySupplier<SoundEvent> DRAWER_OPEN = create("drawer_open");
    public static final RegistrySupplier<SoundEvent> DRAWER_CLOSE = create("drawer_close");
    public static final RegistrySupplier<SoundEvent> CABINET_OPEN = create("cabinet_open");
    public static final RegistrySupplier<SoundEvent> CABINET_CLOSE = create("cabinet_close");
    public static final RegistrySupplier<SoundEvent> TIMER_TICKING = create("timer_ticking");


    private static RegistrySupplier<SoundEvent> create(String name) {
        final ResourceLocation id = new BreweryIdentifier(name);
        return SOUND_EVENTS.register(id, () -> new SoundEvent(id));
    }

    public static void init() {
        Brewery.LOGGER.debug("Registering Sounds for " + Brewery.MOD_ID);
    }
}
