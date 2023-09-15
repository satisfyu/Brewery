package net.bmjo.brewery.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.HashMap;
import java.util.Map;

public class BannerPatternRegistry {
    private static final Map<String, RegistrySupplier<BannerPattern>> PATTERNS = new HashMap<>();

    public static final String BEER_KEG_BANNER = "beer_keg";

    public static void registerBannerPattern() {
        Registry.BANNER_PATTERN.getOptional(new BreweryIdentifier(BEER_KEG_BANNER))
                .ifPresent(supplier -> PATTERNS.put(BEER_KEG_BANNER, (RegistrySupplier<BannerPattern>) supplier));
    }

    public static RegistrySupplier<BannerPattern> getBannerPattern(String patternId) {
        return PATTERNS.get(patternId);
    }
}
