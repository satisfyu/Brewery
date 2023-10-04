package net.bmjo.brewery.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.bmjo.brewery.entity.rope.HangingRopeEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;

public class EntityInteractionEvent implements InteractionEvent.InteractEntity { //TODO or DELETE
    @Override
    public EventResult interact(Player player, Entity entity, InteractionHand hand) {
        if (entity instanceof HangingRopeEntity hangingRopeEntity) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(Items.LANTERN)) {
                player.getLevel().setBlockAndUpdate(hangingRopeEntity.blockPosition().below(), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
            }
        }
        return EventResult.pass();
    }
}
