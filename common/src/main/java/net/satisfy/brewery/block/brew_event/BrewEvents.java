package net.satisfy.brewery.block.brew_event;

import net.satisfy.brewery.util.BreweryIdentifier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BrewEvents {
    public static final Map<ResourceLocation, Supplier<BrewEvent>> BREW_EVENTS = new HashMap<>();

    public static final ResourceLocation KETTLE_EVENT = registerBrewEvent("kettle", KettleEvent::new);
    public static final ResourceLocation OVEN_EVENT = registerBrewEvent("oven", OvenEvent::new);
    public static final ResourceLocation WHISTLE_EVENT = registerBrewEvent("whistle", WhistleEvent::new);
    public static final ResourceLocation TIMER_EVENT = registerBrewEvent("timer", TimerEvent::new);

    public static void loadClass(){
    }

    public static ResourceLocation registerBrewEvent(String id, Supplier<BrewEvent> brewEventSupplier) {
        ResourceLocation resourceLocation = new BreweryIdentifier(id);
        BREW_EVENTS.put(resourceLocation, brewEventSupplier);
        return resourceLocation;
    }

    public static List<ResourceLocation> toLocations(Collection<BrewEvent> events){
        return events.stream().map(BrewEvents::getId).collect(Collectors.toList());
    }

    public static List<BrewEvent> toEvents(List<Supplier<BrewEvent>> events){
        return events.stream().map(Supplier::get).collect(Collectors.toList());
    }

    @Nullable
    public static ResourceLocation getId(BrewEvent event) {
        for (ResourceLocation location : BREW_EVENTS.keySet()) {
            BrewEvent brewEvent = BREW_EVENTS.get(location).get();
            if (event.getClass() == brewEvent.getClass()) {
                return location;
            }
        }
        return null;
    }

    @Nullable
    public static Supplier<BrewEvent> byId(ResourceLocation id) {
        return BREW_EVENTS.get(id);
    }
}
