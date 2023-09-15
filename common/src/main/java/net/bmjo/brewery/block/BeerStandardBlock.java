package net.bmjo.brewery.block;

import net.bmjo.brewery.Brewery;
import net.minecraft.resources.ResourceLocation;

public class BeerStandardBlock extends AbstractStandardBlock {
    public static final ResourceLocation TEXTURE = Brewery.MOD_ID("textures/standard/beer_standard.png");

    public BeerStandardBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ResourceLocation getRenderTexture() {
        return TEXTURE;
    }
}