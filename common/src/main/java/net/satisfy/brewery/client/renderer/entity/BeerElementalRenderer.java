package net.satisfy.brewery.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.brewery.client.model.BeerElementalModel;
import net.satisfy.brewery.core.entity.BeerElementalEntity;
import net.satisfy.brewery.core.util.BreweryIdentifier;
import org.jetbrains.annotations.NotNull;


public class
BeerElementalRenderer extends MobRenderer<BeerElementalEntity, BeerElementalModel<BeerElementalEntity>> {

    private static final ResourceLocation TEXTURE = BreweryIdentifier.identifier("textures/entity/beer_elemental.png");

    public BeerElementalRenderer(EntityRendererProvider.Context context) {
        super(context, new BeerElementalModel<>(context.bakeLayer(BeerElementalModel.BEER_ELEMENTAL_MODEL_LAYER)), 0.7f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BeerElementalEntity entity) {
        return TEXTURE;
    }
}
