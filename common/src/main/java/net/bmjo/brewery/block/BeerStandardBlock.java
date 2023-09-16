package net.bmjo.brewery.block;

import net.bmjo.brewery.Brewery;
import net.bmjo.brewery.entity.StandardBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BeerStandardBlock extends AbstractStandardBlock {
    public static final ResourceLocation TEXTURE = Brewery.MOD_ID("textures/standard/beer_standard.png");

    public BeerStandardBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ResourceLocation getRenderTexture() {
        return TEXTURE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return StandardBlockEntity::tick;
    }
}