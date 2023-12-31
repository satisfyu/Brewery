package net.satisfy.brewery.block.property;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BrewMaterial implements StringRepresentable {
    WOOD("wood"),
    COPPER("copper"),
    NETHERITE("netherite");

    private final String name;

    BrewMaterial(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
