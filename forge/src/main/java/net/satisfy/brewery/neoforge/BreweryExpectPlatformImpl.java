package net.satisfy.brewery.neoforge;


import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class BreweryExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
