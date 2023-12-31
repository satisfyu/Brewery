package net.satisfy.brewery.client.render;

import net.satisfy.brewery.entity.rope.RopeCollisionEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class RopeCollisionEntityRenderer extends EntityRenderer<RopeCollisionEntity> {
    public RopeCollisionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(RopeCollisionEntity entity) {
        return null;
    }
}
