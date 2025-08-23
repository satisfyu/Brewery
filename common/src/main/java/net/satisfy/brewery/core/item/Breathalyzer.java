package net.satisfy.brewery.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.satisfy.brewery.core.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.core.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Breathalyzer extends Item {
    public Breathalyzer(Properties properties) {
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
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            ItemStack itemStack = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
            CompoundTag nbtData = new CompoundTag();
            AlcoholLevel alcoholLevel = alcoholPlayer.brewery$getAlcohol();
            nbtData.putString("brewery.drunkenness", alcoholLevel.isBlackout() ? "DANGER" : alcoholLevel.isDrunk() ? "WARNING" : "EASY");
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtData));
        }
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

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            String drunkenness = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString("brewery.drunkenness");
            Component tooltip = switch (drunkenness) {
                case "DANGER" -> Component.translatable(drunkenness).withStyle(ChatFormatting.RED);
                case "WARNING" -> Component.translatable(drunkenness).withStyle(ChatFormatting.GOLD);
                case "EASY" -> Component.translatable(drunkenness).withStyle(ChatFormatting.GREEN);
                default -> Component.translatable(drunkenness);
            };
            list.add(tooltip);
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
