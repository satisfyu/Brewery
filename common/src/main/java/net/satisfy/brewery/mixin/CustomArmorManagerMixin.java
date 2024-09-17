package net.satisfy.brewery.mixin;

import de.cristelknight.doapi.client.render.feature.CustomArmorFeatureRenderer;
import de.cristelknight.doapi.client.render.feature.CustomArmorManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomArmorFeatureRenderer.class)
public abstract class CustomArmorManagerMixin {

    @Shadow(remap = false) @Final public CustomArmorManager<LivingEntity> ARMORS;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void brewery$armorrenderer(CallbackInfo info) {
//        Brewery.LOGGER.info("Initializing armor renderer");
//        ArmorRegistry.registerArmorModels(ARMORS, ((CustomArmorManagerAccessor) ARMORS).getModelLoader());

    }

//    @Shadow @Final private EntityModelSet modelLoader;
//    @Unique
//    CustomArmorManager brewery$customArmorManager = (CustomArmorManager) (Object) this;
//
//    @Inject(method = "updateArmors", at = @At("HEAD"), remap=false)
//    public void brewery$updateArmors(CallbackInfo ci){
//        Brewery.LOGGER.info("Updating armors");
//        ArmorRegistry.registerArmorModels(brewery$customArmorManager, modelLoader);
//    }
}
