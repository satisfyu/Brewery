package net.satisfy.brewery.entity;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.satisfy.brewery.registry.EntityRegistry;
import net.satisfy.brewery.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class DarkBrewEntity extends ThrowableItemProjectile {

    public DarkBrewEntity(Level world, LivingEntity owner) {
        super(EntityRegistry.DARK_BREW.get(), owner, world);
    }

    public DarkBrewEntity(EntityType<? extends DarkBrewEntity> entityType, Level world) {
        super(entityType, world);
    }

    protected @NotNull Item getDefaultItem() {
        return ObjectRegistry.DARK_BREW.get();
    }

    private ParticleOptions getParticleParameters() {
        ItemStack itemStack = this.getItem();
        ParticleOptions particle = itemStack.isEmpty() ? ParticleTypes.LANDING_HONEY : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
        if (this.level().isClientSide) {
            spawnBlockParticles(Blocks.GLASS_PANE.defaultBlockState(), 100);
            spawnBlockParticles(Blocks.SPRUCE_LOG.defaultBlockState(), 20);
            spawnSimpleParticles();
        }
        return particle;
    }

    private void spawnBlockParticles(BlockState state, int count) {
        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, state);
        for (int i = 0; i < count; i++) {
            this.level().addParticle(particleOption, this.getX(), this.getY(), this.getZ(), random.nextGaussian() * 0.02, 0.2, random.nextGaussian() * 0.02);
        }
    }

    private void spawnSimpleParticles() {
        for (int i = 0; i < 25; i++) {
            double xOffset = random.nextGaussian() * 0.1;
            double zOffset = random.nextGaussian() * 0.1;
            double yOffset = random.nextDouble() * 0.5 + 0.2;
            this.level().addParticle(ParticleTypes.LANDING_HONEY, this.getX() + xOffset, this.getY() + yOffset, this.getZ() + zOffset, xOffset, yOffset, zOffset);
        }
    }

    public void handleEntityEvent(byte status) {
        if (status == 3) {
            ParticleOptions particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        int damage = 2;
        entity.hurt(entity.damageSources().thrown(this, this.getOwner()), (float) damage);
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.playSound(SoundEvents.WOOD_BREAK, 1.0F, 1.0F);
            BeerElementalEntity beerElemental = new BeerElementalEntity(EntityRegistry.BEER_ELEMENTAL.get(), this.level());
            beerElemental.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            this.level().addFreshEntity(beerElemental);

            this.discard();
        }
    }
}

