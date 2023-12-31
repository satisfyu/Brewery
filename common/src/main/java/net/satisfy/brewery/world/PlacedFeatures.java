package net.satisfy.brewery.world;

import net.satisfy.brewery.util.BreweryIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedFeatures {
    public static final ResourceKey<PlacedFeature> WILD_HOPS_KEY = registerKey("wild_hops");


    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new BreweryIdentifier(name));
    }
}
