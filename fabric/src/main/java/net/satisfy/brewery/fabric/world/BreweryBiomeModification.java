package net.satisfy.brewery.fabric.world;

import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.satisfy.brewery.util.BreweryIdentifier;
import net.satisfy.brewery.world.PlacedFeatures;

import java.util.function.Predicate;


public class BreweryBiomeModification {

    public static void init() {
        BiomeModification world = BiomeModifications.create(BreweryIdentifier.of("world_features"));
        Predicate<BiomeSelectionContext> beachBiomes = getBrewerySelector("taiga");


        world.add(ModificationPhase.ADDITIONS, beachBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.WILD_HOPS_KEY));
    }

    private static Predicate<BiomeSelectionContext> getBrewerySelector(String path) {
        return BiomeSelectors.tag(TagKey.create(Registries.BIOME, BreweryIdentifier.of(path)));
    }


}
