package net.bmjo.brewery.sound;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {

    public static Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Brewery.MOD_ID, Registries.SOUND_EVENT).getRegistrar();

    public static final RegistrySupplier<SoundEvent> BREATH = create("breath");

    private static RegistrySupplier<SoundEvent> create(String name) {
        final ResourceLocation id = new BreweryIdentifier(name);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds() {
        Brewery.LOGGER.debug("Register " + SoundRegistry.class);
    }
}
