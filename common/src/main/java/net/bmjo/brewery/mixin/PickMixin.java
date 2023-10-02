package net.bmjo.brewery.mixin;

import net.bmjo.brewery.entity.IRopeEntity;
import net.bmjo.brewery.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class PickMixin {
    @Shadow
    @Nullable
    public HitResult hitResult;

    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;

    @Inject(at = @At("HEAD"), method = "pickBlock", cancellable = true)
    private void pickRope(CallbackInfo ci) {
        if (this.hitResult != null && this.hitResult instanceof EntityHitResult entityHitResult && this.player != null) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof IRopeEntity && this.gameMode != null) {
                ItemStack itemStack = new ItemStack(ObjectRegistry.ROPE.get());
                ci.cancel();
                Inventory inventory = this.player.getInventory();
                int i = inventory.findSlotMatchingItem(itemStack);
                if (this.player.getAbilities().instabuild) {
                    inventory.setPickedItem(itemStack);
                    this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.selected);
                } else if (i != -1) {
                    if (Inventory.isHotbarSlot(i)) {
                        inventory.selected = i;
                    } else {
                        this.gameMode.handlePickItem(i);
                    }
                }
            }
        }
    }

}
