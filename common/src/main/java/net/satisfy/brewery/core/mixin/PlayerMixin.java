package net.satisfy.brewery.core.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.satisfy.brewery.core.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updatePlayerPose", at = @At(value = "HEAD"), cancellable = true)
    protected void drunkPose(CallbackInfo ci) {
        if (this instanceof AlcoholPlayer alcoholPlayer && alcoholPlayer.brewery$getAlcohol().isBlackout()) {
            if (alcoholPlayer instanceof LivingEntity livingEntity && livingEntity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()))) {
                MobEffectInstance effectInstance = livingEntity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.BLACKOUT.get()));
                if (effectInstance.getDuration() <= AlcoholManager.FALL_DOWN) {
                    this.setPose(Pose.SWIMMING);
                    ci.cancel();
                }
            }
        }
    }
}
