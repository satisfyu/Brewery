package net.bmjo.brewery.block.property;

import net.minecraft.util.StringRepresentable;

public enum Heat implements StringRepresentable {
    OFF("off"),
    WEAK("weak"),
    LIT("lit");

    private final String name;

    Heat(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
