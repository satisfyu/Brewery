package net.satisfy.brewery.util.rope;

public record UVCord(float x0, float x1, float y0, float y1) {

    public static final UVCord DEFAULT_ROPE_H = new UVCord(0, 3, 0, 16);

    public static final UVCord DEFAULT_ROPE_V = new UVCord(3, 6, 0, 16);
}