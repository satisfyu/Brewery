package net.satisfy.brewery.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class BreweryExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
