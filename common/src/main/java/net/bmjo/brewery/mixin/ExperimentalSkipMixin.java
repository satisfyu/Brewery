package net.bmjo.brewery.mixin;

import com.mojang.serialization.Lifecycle;
import dev.architectury.platform.Platform;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.WorldData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldOpenFlows.class)
public class ExperimentalSkipMixin {
    @Redirect(method = "doLoadLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;", opcode = Opcodes.PUTFIELD))
    private Lifecycle injected(WorldData worldData) {
        return Platform.isDevelopmentEnvironment() ? Lifecycle.stable() : worldData.worldGenSettingsLifecycle();
    }
}
