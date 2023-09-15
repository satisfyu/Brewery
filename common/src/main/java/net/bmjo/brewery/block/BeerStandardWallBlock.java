package net.bmjo.brewery.block;

import net.minecraft.resources.ResourceLocation;


public class BeerStandardWallBlock extends AbstractStandardWallBlock {
    public BeerStandardWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ResourceLocation getRenderTexture() {
        return BeerStandardBlock.TEXTURE;
    }
}