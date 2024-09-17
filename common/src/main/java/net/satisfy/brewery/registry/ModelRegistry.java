package net.satisfy.brewery.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.satisfy.brewery.util.BreweryIdentifier;

@SuppressWarnings("unused")
public class ModelRegistry {
    public static final ModelLayerLocation ROPE_KNOT = new ModelLayerLocation(BreweryIdentifier.of("rope_knot"), "main");
    public static final ModelLayerLocation BEER_ELEMENTAL = new ModelLayerLocation(BreweryIdentifier.of("beer_elemental"), "main");
    public static final ModelLayerLocation BEER_ELEMENTAL_ATTACK = new ModelLayerLocation(BreweryIdentifier.of("beer_elemental_attack"), "main");
    public static final ModelLayerLocation HANGING_ROPE = new ModelLayerLocation(BreweryIdentifier.of("hanging_rope"), "main");
    public static final ModelLayerLocation ROPE_COLLISION = new ModelLayerLocation(BreweryIdentifier.of("rope_collision"), "main");
}
