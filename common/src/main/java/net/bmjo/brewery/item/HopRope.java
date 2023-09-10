package net.bmjo.brewery.item;

import net.bmjo.brewery.entity.HopRopeKnotEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HopRope extends Item {
    private static final String ATTACHED_KEY = "brewery.attached";

    public HopRope(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        Player player = useOnContext.getPlayer();
        InteractionHand hand = useOnContext.getHand();
        if (blockState.is(BlockTags.FENCES)) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            HopRopeKnotEntity knot = HopRopeKnotEntity.getHopRopeKnotEntity(level, blockPos);
            if (knot != null) {
                if (knot.interact(player, hand) == InteractionResult.CONSUME) {
                    return InteractionResult.CONSUME;
                }
                return InteractionResult.PASS;
            }

            HopRopeKnotEntity hopRopeKnotEntity = HopRopeKnotEntity.create(level, blockPos);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return hopRopeKnotEntity.interact(player, hand);
        } else {
            return InteractionResult.PASS;
        }
    }

    private boolean isAttached(@NotNull ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        return tag != null && tag.contains(ATTACHED_KEY);
    }

    private void attachFirstPoint(@NotNull ItemStack itemStack, Level level, @NotNull BlockPos blockPos, Player player) {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray(ATTACHED_KEY, new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
        itemStack.setTag(tag);
    }

    private void attachOrRemovePoint(Player player, Level level, InteractionHand hand, @NotNull BlockPos blockPos, ItemStack itemStack) {
        HopRopeKnotEntity hopRopeKnotEntity = HopRopeKnotEntity.getHopRopeKnotEntity(level, blockPos);
        if (hopRopeKnotEntity != null) {
            hopRopeKnotEntity.interact(player, hand);
        }
        removeFirstPoint(itemStack);
    }

    private void removeFirstPoint(@NotNull ItemStack itemStack) {
        itemStack.setTag(new CompoundTag());
    }

    private @Nullable BlockPos getAttachedPoint(@NotNull ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return null;
        int[] coordinates = tag.getIntArray(ATTACHED_KEY);
        return coordinates.length == 3 ? new BlockPos(coordinates[0], coordinates[1], coordinates[2]) : null;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.hasTag()) {
            int[] blockPos = itemStack.getTag().getIntArray(ATTACHED_KEY);
            if (blockPos.length == 3) {
                list.add(Component.literal("Connected to: X: " + blockPos[0] + " Y: " + blockPos[1] + " Z: " + blockPos[2]).withStyle(ChatFormatting.BLUE));
            }
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
