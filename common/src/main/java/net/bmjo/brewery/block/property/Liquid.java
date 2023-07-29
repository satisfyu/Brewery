package net.bmjo.brewery.block.property;

import net.minecraft.util.StringRepresentable;

public enum Liquid implements StringRepresentable {
    EMPTY("empty"),
    DRAINED("drained"),
    FILLED("filled"),
    OVERFLOWING("overflowing");

    private final String name;

    Liquid(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
