package net.satisfy.brewery.core.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PartyStarterEvent implements PlayerEvent.AttackEntity {
    private final Random random = new Random();

    @Override
    public EventResult attack(Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) {
        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.PARTYSTARTER.get()))) {
            if (target instanceof LivingEntity entity) {
                int numExplosions = 3;

                for (int i = 0; i < numExplosions; i++) {
                    int color = random.nextInt(0xFFFFFF);

                    ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
                    CompoundTag fireworkNbt = new CompoundTag();
                    ListTag explosions = new ListTag();
                    CompoundTag explosion = new CompoundTag();

                    explosion.putIntArray("Colors", new int[]{color});
                    explosion.putByte("Type", (byte) 0);
                    explosions.add(explosion);
                    fireworkNbt.put("Explosions", explosions);
                    fireworkNbt.putByte("Flight", (byte) 0);
                    fireworkStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().put("Explosions", explosions);

                    FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(level, fireworkStack, entity);
                    fireworkRocket.setAirSupply(0);
                    level.addFreshEntity(fireworkRocket);
                }

                return EventResult.pass();
            }
        }

        return EventResult.pass();
    }
}