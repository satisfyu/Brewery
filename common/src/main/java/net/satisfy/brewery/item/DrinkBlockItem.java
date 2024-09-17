package net.satisfy.brewery.item;

import de.cristelknight.doapi.common.block.entity.StorageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.effect.alcohol.AlcoholManager;
import net.satisfy.brewery.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DrinkBlockItem extends BlockItem {
    private final Holder<MobEffect> effect;
    private final int baseDuration;

    public DrinkBlockItem(Holder<MobEffect> effect, int duration, Block block, Properties settings) {
        super(block, settings);
        this.effect = effect;
        this.baseDuration = duration;
    }

    public static void addQuality(ItemStack itemStack, int quality) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbtData = customData.copyTag();
        nbtData.putInt("brewery.beer_quality", Math.min(Math.max(quality, 0), 3));

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtData));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        if (!Objects.requireNonNull(context.getPlayer()).isCrouching()) {
            return null;
        }

        BlockState blockState = this.getBlock().getStateForPlacement(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        if (level.getBlockEntity(blockPos) instanceof StorageBlockEntity wineEntity) {
            wineEntity.setStack(0, itemStack.copyWithCount(1));
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return ItemUtils.startUsingInstantly(level, player, interactionHand);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack returnStack = super.finishUsingItem(itemStack, level, livingEntity);
        if (livingEntity instanceof Player player && !player.isCreative()) {
            player.addItem(new ItemStack(ObjectRegistry.BEER_MUG.get()));
        }
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            AlcoholManager.drinkAlcohol(serverPlayer);

            CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if(customData.contains("brewery.beer_quality")) {
                int quality = customData.copyTag().getInt("brewery.beer_quality");
                MobEffectInstance effectInstance = calculateEffectForQuality(quality);
                serverPlayer.addEffect(effectInstance);
            } else {
                MobEffectInstance effectInstance = new MobEffectInstance(effect, baseDuration, 0);
                serverPlayer.addEffect(effectInstance);
            }
        }

        return returnStack;
    }

    @NotNull
    private MobEffectInstance calculateEffectForQuality(int quality) {
        int durationMultiplier = 1;
        int effectLevel = switch (quality) {
            case 2 -> {
                durationMultiplier = 3;
                yield 2;
            }
            case 3 -> {
                durationMultiplier = 5;
                yield 3;
            }
            default -> 1;
        };

        return new MobEffectInstance(effect, baseDuration * durationMultiplier, effectLevel - 1);
    }

    public void addCount(ItemStack resultSack, int solved) {
        resultSack.setCount(solved);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        int beerQuality = customData.contains("brewery.beer_quality") ? customData.copyTag().getInt("brewery.beer_quality") : 1;
        int durationMultiplier = 1;

        int effectLevel = switch (beerQuality) {
            case 2 -> {
                durationMultiplier = 3;
                yield 2;
            }
            case 3 -> {
                durationMultiplier = 5;
                yield 3;
            }
            default -> 1;
        };

        if (this.effect != null) {
            MutableComponent effectName = Component.translatable(this.effect.value().getDescriptionId());
            if (effectLevel > 1) {
                effectName.append(" ").append(Component.translatable("potion.potency." + (effectLevel - 1)));
            }
            String durationText = MobEffectUtil.formatDuration(new MobEffectInstance(this.effect, this.baseDuration * durationMultiplier), 1.0f, tooltipContext.tickRate()).getString();
            MutableComponent effectDuration = Component.translatable(" (").append(Component.translatable(durationText)).append(Component.translatable(")"));
            tooltip.add(effectName.append(effectDuration).withStyle(this.effect.value().getCategory().getTooltipFormatting()));
        } else {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        }

        if (beerQuality > 1) {
            tooltip.add(Component.translatable("tooltip.brewery.beer_quality", beerQuality).withStyle(ChatFormatting.GOLD));
        }

    }
}
