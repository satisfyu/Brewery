package net.satisfy.brewery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.satisfy.brewery.Brewery;

public class SoundEventRegistry {
    public static final Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Brewery.MOD_ID, Registries.SOUND_EVENT).getRegistrar();

    public static final RegistrySupplier<SoundEvent> BREATH = create("breath");
    public static final RegistrySupplier<SoundEvent> BREWSTATION_OVEN = create("brewstation_oven");
    public static final RegistrySupplier<SoundEvent> BREWSTATION_KETTLE = create("brewstation_kettle");
    public static final RegistrySupplier<SoundEvent> BREWSTATION_AMBIENT = create("brewstation_ambient");
    public static final RegistrySupplier<SoundEvent> BREWSTATION_WHISTLE = create("brewstation_whistle");
    public static final RegistrySupplier<SoundEvent> BREWSTATION_TIMER_LOOP = create("brewstation_timer_loop");
    public static final RegistrySupplier<SoundEvent> BREWSTATION_PROCESS_FAILED = create("brewstation_process_failed");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_ATTACK = create("beer_elemental_attack");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_AMBIENT = create("beer_elemental_ambient");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_DEATH = create("beer_elemental_death");
    public static final RegistrySupplier<SoundEvent> BEER_ELEMENTAL_HURT = create("beer_elemental_hurt");
    public static final RegistrySupplier<SoundEvent> CABINET_OPEN = create("cabinet_open");
    public static final RegistrySupplier<SoundEvent> DRAWER_OPEN = create("drawer_open");
    public static final RegistrySupplier<SoundEvent> CABINET_CLOSE = create("cabinet_close");
    public static final RegistrySupplier<SoundEvent> DRAWER_CLOSE = create("drawer_close");

    private static RegistrySupplier<SoundEvent> create(String name) {
        final ResourceLocation id = Brewery.identifier(name);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {}
}
