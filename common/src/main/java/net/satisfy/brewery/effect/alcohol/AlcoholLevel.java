package net.satisfy.brewery.effect.alcohol;

import java.util.Objects;

public class AlcoholLevel {
    private int drunkenness;
    private int immunity;
    private final int MAX_IMMUNITY = 10;

    public AlcoholLevel() {
        this(0, 3);
    }

    public AlcoholLevel(int drunkenness, int immunity) {
        this.drunkenness = drunkenness;
        this.immunity = Math.max(immunity, 3);
    }

    public int getDrunkenness() {
        return drunkenness;
    }

    public int getImmunity() {
        return immunity;
    }

    public boolean isDrunk() {
        return drunkenness >= immunity;
    }

    public boolean isBlackout() {
        return this.drunkenness > immunity;
    }

    public boolean isSober() {
        return this.drunkenness == 0;
    }

    public void drink() {
        this.drink(1);
    }

    public void drink(int amount) {
        this.drunkenness += amount;
    }

    public void sober() {
        this.sober(1);
    }

    public void sober(int amount) {
        this.drunkenness = Math.max(this.drunkenness - amount, 0);
    }

    public void soberUp() {
        this.drunkenness = 0;
    }

    public void gainImmunity() {
        this.immunity = Math.min(this.immunity + 1, MAX_IMMUNITY);
    }

    public AlcoholLevel copy() {
        return new AlcoholLevel(this.drunkenness, this.immunity);
    }

    @Override
    public String toString() {
        return "AlcoholLevel: " + "drunkenness=" + drunkenness + ", immunity=" + immunity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlcoholLevel that)) return false;
        return drunkenness == that.drunkenness && immunity == that.immunity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(drunkenness, immunity);
    }
}
