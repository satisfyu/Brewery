package net.satisfy.brewery.util;

import net.satisfy.brewery.Brewery;
import net.minecraft.resources.ResourceLocation;

public class BreweryIdentifier extends ResourceLocation {
    public BreweryIdentifier(String id) {
        super(Brewery.MOD_ID, id);
    }
}
