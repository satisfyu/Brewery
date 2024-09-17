package net.satisfy.brewery.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.Brewery;

public final class BreweryIdentifier {
    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private BreweryIdentifier() {
    }
}
