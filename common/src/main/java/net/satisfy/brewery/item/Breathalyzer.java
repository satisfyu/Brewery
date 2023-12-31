package net.satisfy.brewery.item;

import net.satisfy.brewery.effect.alcohol.AlcoholLevel;
import net.satisfy.brewery.effect.alcohol.AlcoholPlayer;
import net.satisfy.brewery.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Breathalyzer extends Item {
    public Breathalyzer(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        itemStack.setTag(new CompoundTag());
        player.startUsingItem(interactionHand);
        player.awardStat(Stats.ITEM_USED.get(this));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.BREATH.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        return InteractionResultHolder.consume(itemStack);
    }

    private void addNbt(LivingEntity livingEntity) {
        if (livingEntity instanceof AlcoholPlayer alcoholPlayer) {
            ItemStack itemStack = livingEntity.getItemInHand(livingEntity.getUsedItemHand());
            CompoundTag nbtData = new CompoundTag();
            AlcoholLevel alcoholLevel = alcoholPlayer.getAlcohol();
            nbtData.putString("brewery.drunkenness", alcoholLevel.isBlackout() ? "DANGER" : alcoholLevel.isDrunk() ? "WARNING" : "EASY");
            itemStack.setTag(nbtData);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 5 * 20;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int tick) {
        if (getUseDuration(itemStack) - tick == 3 * 20) {
            addNbt(livingEntity);
        }
        super.onUseTick(level, livingEntity, itemStack, tick);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.hasTag()) {
            String drunkenness = itemStack.getTag().getString("brewery.drunkenness");
            Component tooltip = switch (drunkenness) {
                case "DANGER" -> Component.literal(drunkenness).withStyle(ChatFormatting.RED);
                case "WARNING" -> Component.literal(drunkenness).withStyle(ChatFormatting.GOLD);
                case "EASY" -> Component.literal(drunkenness).withStyle(ChatFormatting.GREEN);
                default -> Component.literal(drunkenness);
            };
            list.add(tooltip);
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
