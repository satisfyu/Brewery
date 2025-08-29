package net.satisfy.brewery.core.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BeerElementalEntity extends Monster {
    private float allowedHeightOffset = 0.5f;
    private int nextHeightOffsetChangeTick;

    public BeerElementalEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(1F, 2F);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new BeerElementalAttackGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public void aiStep() {
        if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        if (this.level().isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level().playLocalSound(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.UNDERWATER, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }

        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        nextHeightOffsetChangeTick--;
        if (nextHeightOffsetChangeTick <= 0) {
            nextHeightOffsetChangeTick = 100;
            allowedHeightOffset = (float) random.triangle(0.5D, 6.891D);
        }

        LivingEntity target = getTarget();
        if (target != null) {
            if (target.getEyeY() > getEyeY() + (double) this.allowedHeightOffset) {
                if (canAttack(target)) {
                    Vec3 velocity = getDeltaMovement();
                    velocity = velocity.add(0.0D, (0.3D - velocity.y) * 0.3D, 0.0D);
                    setDeltaMovement(velocity);
                    hasImpulse = true;
                }
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEventRegistry.BEER_ELEMENTAL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEventRegistry.BEER_ELEMENTAL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEventRegistry.BEER_ELEMENTAL_DEATH.get();
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        return false;
    }


    private static class BeerElementalAttackGoal extends Goal {
        private final BeerElementalEntity elemental;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public BeerElementalAttackGoal(BeerElementalEntity elemental) {
            this.elemental = elemental;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = elemental.getTarget();
            return target != null && target.isAlive() && elemental.canAttack(target);
        }

        @Override
        public void start() {
            attackStep = 0;
        }

        @Override
        public void stop() {
            lastSeen = 0;
        }

        @Override
        public void tick() {
            attackTime--;

            LivingEntity target = elemental.getTarget();
            if (target == null) return;

            boolean canSee = elemental.getSensing().hasLineOfSight(target);
            lastSeen = canSee ? 0 : lastSeen + 1;

            double dist = elemental.distanceToSqr(target);
            double range = getFollowDistance() * getFollowDistance();

            if (dist < 4.0D) {
                if (!canSee) return;

                if (attackTime <= 0.0D) {
                    attackTime = 20;
                    elemental.doHurtTarget(target);
                }

                elemental.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);

            } else if (dist < range && canSee) {
                if (attackTime <= 0) {
                    attackStep++;

                    if (attackStep == 1) {
                        attackTime = 60;
                    } else if (attackStep <= 4) {
                        attackTime = 6;
                    } else {
                        attackTime = 100;
                        attackStep = 0;
                    }

                    if (attackStep > 1) {
                        if (!elemental.isSilent()) {
                            elemental.playSound(SoundEventRegistry.BEER_ELEMENTAL_ATTACK.get(), 1.0F, 1.0F);
                        }

                        double dX = target.getX() - elemental.getX();
                        double dY = target.getY(0.5D) - elemental.getY(0.5D);
                        double dZ = target.getZ() - elemental.getZ();

                        double f = Math.sqrt(Math.sqrt(dist)) * 0.5D;
                        Vec3 velocity = new Vec3(
                                elemental.getRandom().triangle(dX, 2.297D * f),
                                dY,
                                elemental.getRandom().triangle(dZ, 2.297D * f)
                        );

                        BeerElementalAttackEntity attack = new BeerElementalAttackEntity(
                                elemental.level(),
                                elemental.getX(),
                                elemental.getY(0.5D) + 0.5D,
                                elemental.getZ(),
                                velocity
                        );

                        elemental.level().addFreshEntity(attack);
                    }
                }

                elemental.getLookControl().setLookAt(target, 10.0F, 10.0F);

            } else if (lastSeen < 5) {
                elemental.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
            }

            super.tick();
        }

        private double getFollowDistance() {
            return elemental.getAttributeValue(Attributes.FOLLOW_RANGE);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

    }
}
