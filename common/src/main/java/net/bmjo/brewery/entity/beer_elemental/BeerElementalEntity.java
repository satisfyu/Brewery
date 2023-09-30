package net.bmjo.brewery.entity.beer_elemental;

import net.bmjo.brewery.sound.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;

public class BeerElementalEntity extends Blaze {
    public BeerElementalEntity(EntityType<? extends Blaze> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Blaze.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    protected SoundEvent getAmbientSound() {
        return SoundRegistry.BEER_ELEMENTAL_AMBIENT.get();    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.BEER_ELEMENTAL_HURT.get();    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.BEER_ELEMENTAL_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public void aiStep() {
        if (!this.onGround && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }

        super.aiStep();
    }
}
