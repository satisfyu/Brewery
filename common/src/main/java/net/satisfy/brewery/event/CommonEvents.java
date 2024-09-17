package net.satisfy.brewery.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.EntityHitResult;
import net.satisfy.brewery.registry.MobEffectRegistry;
import net.satisfy.brewery.util.BreweryLoottableInjector;
import org.jetbrains.annotations.Nullable;

public class CommonEvents {
    public static void init() {
        LootEvent.MODIFY_LOOT_TABLE.register(CommonEvents::onModifyLootTable);
        PlayerEvent.ATTACK_ENTITY.register(CommonEvents::onPlayerAttack);
    }

    private static void onModifyLootTable(ResourceKey<LootTable> lootTableResourceKey, LootEvent.LootTableModificationContext context, boolean b) {
        BreweryLoottableInjector.InjectLoot(lootTableResourceKey.location(), context);
    }

    public static EventResult onPlayerAttack(Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) {
        if (player.hasEffect(MobEffectRegistry.PROTECTIVETOUCH)) {
            handleProtectiveTouch(level, target);
            return EventResult.interruptFalse();
        }
        if (player.hasEffect(MobEffectRegistry.HEALINGTOUCH)) {
            handleHealingTouch(level, target);
            return EventResult.interruptFalse();
        }
        if (player.hasEffect(MobEffectRegistry.RENEWINGTOUCH)) {
            handleRenewingTouch(level, target);
            return EventResult.interruptFalse();
        }
        if (player.hasEffect(MobEffectRegistry.TOXICTOUCH)) {
            handleToxicTouch(level, target);
            return EventResult.pass();
        }
        if (player.hasEffect(MobEffectRegistry.LIGHTNING_STRIKE)) {
            handlelightningStrike(level, target);
            return EventResult.pass();
        }
        if (player.hasEffect(MobEffectRegistry.EXPLOSION)) {
            handleExplosiveTouch(level, target, player);
            return EventResult.pass();
        }

        return EventResult.pass();
    }

    private static void handleRenewingTouch(Level level, Entity target) {
        if (target instanceof LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            spawnParticles(level, entity, ParticleTypes.COMPOSTER);
        }
    }

    private static void handleProtectiveTouch(Level level, Entity target) {
        if (target instanceof LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
            spawnParticles(level, entity, ParticleTypes.GLOW_SQUID_INK);
        }
    }

    private static void handleToxicTouch(Level level, Entity target) {
        if (target instanceof LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 2));
            spawnParticles(level, entity, ParticleTypes.SCRAPE);
        }
    }

    private static void handleHealingTouch(Level level, Entity target) {
        if (target instanceof LivingEntity entity) {
            ((LivingEntity) target).heal(6.0f);
            spawnParticles(level, entity, ParticleTypes.HEART);
        }
    }

    private static void handlelightningStrike(Level level, Entity target) {
        float chance = level.isThundering() ? 0.75f : 0.1f;
        if (level.random.nextFloat() < chance) {
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            lightningBolt.moveTo(target.getX(), target.getY(), target.getZ());
            level.addFreshEntity(lightningBolt);
        }
    }

    private static void handleExplosiveTouch(Level level, Entity target, Player player) {
        if (level.random.nextFloat() < 0.1) {
            Fireball fireball = EntityType.FIREBALL.create(level);
            double dx = target.getX() - player.getX();
            double dy = target.getY() + target.getBbHeight() / 2 - (player.getY() + player.getBbHeight() / 2); // Differenz in Y Richtung
            double dz = target.getZ() - player.getZ();
            assert fireball != null;
            fireball.setPos(player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ());
            fireball.shoot(dx, dy, dz, 1.5f, 0);
            level.addFreshEntity(fireball);
        }
    }


    private static void spawnParticles(Level level, Entity target, ParticleOptions particleType) {
        double height = target.getBbHeight();
        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();

        for (double i = 2.5; i >= 1.5; i -= 0.5) {
            level.addParticle(particleType, x, y + height / i, z, 0, 0, 0);
        }
    }
}
