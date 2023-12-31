package net.satisfy.brewery.sound;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {

    public static Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Brewery.MOD_ID, Registry.SOUND_EVENT_REGISTRY).getRegistrar();

    public static final RegistrySupplier<SoundEvent> BREATH = create("breath");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_AMBIENT = create("beer_elemental_ambient");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_HURT = create("beer_elemental_hurt");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_DEATH = create("beer_elemental_death");

    private static RegistrySupplier<SoundEvent> create(String name) {
        final ResourceLocation id = new BreweryIdentifier(name);
        return SOUND_EVENTS.register(id, () -> new SoundEvent(id));
    }

    public static void registerSounds() {
        Brewery.LOGGER.debug("Register " + SoundRegistry.class);
    }
}
