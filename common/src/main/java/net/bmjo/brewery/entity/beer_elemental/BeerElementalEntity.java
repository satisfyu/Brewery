package net.bmjo.brewery.entity.beer_elemental;

import net.bmjo.brewery.entity.beer_elemental_barrel.BeerElementalAttackEntity;
import net.bmjo.brewery.sound.SoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BeerElementalEntity extends Monster {

    private float allowedHeightOffset = 0.5f;
    private int nextHeightOffsetChangeTick;

    public BeerElementalEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
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

    @Override
    public void aiStep() {
        if (!onGround && getDeltaMovement().y < 0.0D)
            setDeltaMovement(getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));

        if (level.isClientSide) {
            if (random.nextInt(24) == 0 && !isSilent())
                level.playLocalSound(getX(), getY(), getZ(), SoundRegistry.BEER_ELEMENTAL_AMBIENT.get(), getSoundSource(), 1.0F + random.nextFloat(), 0.3F + random.nextFloat() * 0.7F, false);

            for(int i = 0; i < 2; i++) {
                double velocityX = (random.nextDouble() - 0.5) * 0.1;
                double velocityY = random.nextDouble() * 0.2;
                double velocityZ = (random.nextDouble() - 0.5) * 0.1;

                double slowerVelocityX = velocityX * 0.001;
                double slowerVelocityY = velocityY * 0.001;
                double slowerVelocityZ = velocityZ * 0.001;

                level.addParticle(ParticleTypes.UNDERWATER, getRandomX(0.3D), getY() + random.nextDouble() * 1.4D, getRandomZ(0.4D), slowerVelocityX, slowerVelocityY, slowerVelocityZ);
            }
        }

        super.aiStep();
    }


    @Override
    protected void customServerAiStep() {
        nextHeightOffsetChangeTick--;
        if (nextHeightOffsetChangeTick <= 0) {
            nextHeightOffsetChangeTick = 100;
            allowedHeightOffset = (float)random.triangle(0.5D, 6.891D);
        }

        LivingEntity target = getTarget();
        if (target != null) {
            if(target.getEyeY() > getEyeY() + (double)this.allowedHeightOffset) {
                if(canAttack(target)) {
                    Vec3 velocity = getDeltaMovement();
                    velocity = velocity.add(0.0D, (0.3D - velocity.y) * 0.3D, 0.0D);
                    setDeltaMovement(velocity);
                    hasImpulse = true;
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.BEER_ELEMENTAL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.BEER_ELEMENTAL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.BEER_ELEMENTAL_DEATH.get();
    }


    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
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
            if (target == null)
                return;

            boolean canSee = elemental.getSensing().hasLineOfSight(target);
            lastSeen = canSee ? 0 : lastSeen + 1;

            double dist = elemental.distanceToSqr(target);
            double range = getFollowDistance() * getFollowDistance();
            if (dist < 4.0D) {
                if (!canSee)
                    return;

                if (attackTime <= 0.0D) {
                    attackTime = 20;
                    elemental.doHurtTarget(target);
                }
                elemental.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);

            } else if (dist < range && canSee) {
                if (attackTime <= 0) {
                    attackStep++;

                    if (attackStep == 1)
                        attackTime = 60;
                    else if (attackStep <= 4)
                        attackTime = 6;
                    else {
                        attackTime = 100;
                        attackStep = 0;
                    }

                    if (attackStep > 1) {
                        if (!elemental.isSilent())
                            elemental.level.levelEvent(null, 1018, elemental.blockPosition(), 0);

                        double dX = target.getX() - elemental.getX();
                        double dY = target.getY(0.5D) - elemental.getY(0.5D);
                        double dZ = target.getZ() - elemental.getZ();

                        double f = Math.sqrt(Math.sqrt(dist)) * 0.5D;

                        for (int i = 0; i < 1; ++i) {
                            BeerElementalAttackEntity attack = new BeerElementalAttackEntity(elemental.level, elemental, elemental.getRandom().triangle(dX, 2.297D * f), dY, elemental.getRandom().triangle(dZ, 2.297D * f));
                            attack.setPos(attack.getX(), elemental.getY(0.5D) + 0.5D, attack.getZ());


                            elemental.level.addFreshEntity(attack);
                        }
                    }
                }
                elemental.getLookControl().setLookAt(target, 10.0F, 10.0F);
            }
            else if (lastSeen < 5)
                elemental.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);

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
