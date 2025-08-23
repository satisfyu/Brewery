package net.satisfy.brewery.core.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.Brewery;

public class BreweryIdentifier {

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(Brewery.MOD_ID, name);
    }
}
