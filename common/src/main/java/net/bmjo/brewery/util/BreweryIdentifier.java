package net.bmjo.brewery.util;

import net.bmjo.brewery.Brewery;
import net.minecraft.resources.ResourceLocation;

public class BreweryIdentifier extends ResourceLocation {
    public BreweryIdentifier(String id) {
        super(Brewery.MOD_ID, id);
    }
}
