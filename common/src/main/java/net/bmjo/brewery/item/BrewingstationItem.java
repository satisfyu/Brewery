package net.bmjo.brewery.item;

import net.bmjo.brewery.block.property.BrewMaterial;
import net.bmjo.brewery.registry.BlockStateRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrewingstationItem extends BlockItem {
    private final BrewMaterial brewMaterial;

    public BrewingstationItem(Block block, BrewMaterial brewMaterial, Properties properties) {
        super(block, properties);
        this.brewMaterial = brewMaterial;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = this.getBlock().getStateForPlacement(blockPlaceContext);
        return blockState != null && this.canPlace(blockPlaceContext, blockState) ? blockState.setValue(BlockStateRegistry.MATERIAL, this.brewMaterial) : null;
    }
}
