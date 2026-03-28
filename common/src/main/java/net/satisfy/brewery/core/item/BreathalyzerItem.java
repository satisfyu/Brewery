package net.satisfy.brewery.core.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreathalyzerItem extends Item {
    private static boolean initialized = false;

    public BreathalyzerItem(Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        player.startUsingItem(interactionHand);
        player.awardStat(Stats.ITEM_USED.get(this));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEventRegistry.BREATH.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        return InteractionResultHolder.consume(itemStack);
    }

    private void addNbt(LivingEntity livingEntity) {
        ItemStack itemStack = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
        Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.DRUNK.get());
        var inst = livingEntity.getEffect(holder);
        int amplifier = inst != null ? inst.getAmplifier() : -1;
        String drunkennessValue = amplifier <= 1 ? "EASY" : amplifier <= 3 ? "WARNING" : "DANGER";
        CompoundTag nbtData = new CompoundTag();
        nbtData.putString("brewery.drunkenness", drunkennessValue);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtData));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 5 * 20;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int tick) {
        if (getUseDuration(itemStack, livingEntity) - tick == 3 * 20) {
            addNbt(livingEntity);
        }
        super.onUseTick(level, livingEntity, itemStack, tick);
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), Brewery.identifier("breathing"), (itemStack, clientLevel, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        ItemPropertiesRegistry.register(ObjectRegistry.BREATHALYZER.get(), Brewery.identifier("drunkenness"), (itemStack, clientLevel, livingEntity, seed) -> {
            if (itemStack.has(DataComponents.CUSTOM_DATA)) {
                String drunkennessValue = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("brewery.drunkenness");
                return switch (drunkennessValue) {
                    case "DANGER" -> 0.9F;
                    case "WARNING" -> 0.6F;
                    case "EASY" -> 0.3F;
                    default -> 0.0F;
                };
            }
            return 0.0F;
        });
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            String drunkennessValue = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("brewery.drunkenness");
            Component tooltip = switch (drunkennessValue) {
                case "DANGER" -> Component.translatable(drunkennessValue).withStyle(ChatFormatting.RED);
                case "WARNING" -> Component.translatable(drunkennessValue).withStyle(ChatFormatting.GOLD);
                case "EASY" -> Component.translatable(drunkennessValue).withStyle(ChatFormatting.GREEN);
                default -> Component.translatable(drunkennessValue);
            };
            list.add(tooltip);
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}