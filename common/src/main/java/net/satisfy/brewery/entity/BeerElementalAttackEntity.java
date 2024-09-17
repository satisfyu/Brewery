package net.satisfy.brewery.entity;

import de.cristelknight.doapi.common.registry.DoApiSoundEventRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.brewery.registry.EntityRegistry;
import org.jetbrains.annotations.NotNull;

public class BeerElementalAttackEntity extends AbstractHurtingProjectile {

    public BeerElementalAttackEntity(EntityType<? extends BeerElementalAttackEntity> entityType, Level level) {
        super(entityType, level);
        playCreationSound();
    }

    public BeerElementalAttackEntity(Level level, LivingEntity livingEntity, double d, double e, double f) {
        super(EntityRegistry.BEER_ELEMENTAL_ATTACK.get(), livingEntity, new Vec3(d, e, f), level);

        double velocityModifier = 0.4;
        this.setDeltaMovement(this.getDeltaMovement().multiply(velocityModifier, velocityModifier, velocityModifier));

    }

    private void playCreationSound() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        SoundEvent soundEvent = DoApiSoundEventRegistry.BEER_ELEMENTAL_ATTACK.get();

        this.getCommandSenderWorld().playSound(null, x, y, z, soundEvent, this.getSoundSource(), 1.0F, 1.0F);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.hurt(entity.damageSources().thrown(this, null), 6);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.getCommandSenderWorld().isClientSide)
            this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle() {
        return ParticleTypes.FALLING_HONEY;
    }
}
