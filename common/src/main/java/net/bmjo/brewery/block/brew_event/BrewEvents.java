package net.bmjo.brewery.block.brew_event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BrewEvents {
    public static final List<Supplier<BrewEvent>> BREW_EVENTS = new ArrayList<>();

    private static final Supplier<BrewEvent> BASIN_EVENT = registerBrewEvent(BasinEvent::new);
    private static final Supplier<BrewEvent> OVEN_EVENT = registerBrewEvent(OvenEvent::new);
    private static final Supplier<BrewEvent> WHISTLE_EVENT = registerBrewEvent(WhistleEvent::new);
    private static final Supplier<BrewEvent> TIMER_EVENT = registerBrewEvent(TimerEvent::new);

    public static Supplier<BrewEvent> registerBrewEvent(Supplier<BrewEvent> brewEventSupplier) {
        BREW_EVENTS.add(brewEventSupplier);
        return brewEventSupplier;
    }
}
