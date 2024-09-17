package net.satisfy.brewery.mixin;

import de.cristelknight.doapi.client.render.feature.CustomArmorManager;
import net.minecraft.client.model.geom.EntityModelSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CustomArmorManager.class)
public interface CustomArmorManagerAccessor {
    @Accessor
    EntityModelSet getModelLoader();
}
