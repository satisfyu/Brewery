package net.satisfy.brewery.item;

import de.cristelknight.doapi.common.registry.DoApiSoundEventRegistry;
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
import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Breathalyzer extends Item {
    public Breathalyzer(Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        player.awardStat(Stats.ITEM_USED.get(this));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), DoApiSoundEventRegistry.BREATH.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        return InteractionResultHolder.consume(itemStack);
    }

    private void addNbt(LivingEntity livingEntity) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            ItemStack itemStack = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
            CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();

            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            tag.putString("brewery.drunkenness", alcoholLevel.isBlackout() ? "DANGER" : alcoholLevel.isDrunk() ? "WARNING" : "EASY");

            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
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
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (tag.contains("brewery.drunkenness")) {
            String drunkenness = tag.getString("brewery.drunkenness");
            Component tooltip = switch (drunkenness) {
                case "DANGER" -> Component.translatable(drunkenness).withStyle(ChatFormatting.RED);
                case "WARNING" -> Component.translatable(drunkenness).withStyle(ChatFormatting.GOLD);
                case "EASY" -> Component.translatable(drunkenness).withStyle(ChatFormatting.GREEN);
                default -> Component.translatable(drunkenness);
            };
            list.add(tooltip);
        }
    }
}
