package net.bmjo.brewery.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.bmjo.brewery.client.render.HangingRopeRenderer;
import net.bmjo.brewery.client.render.RopeCollisionEntityRenderer;
import net.bmjo.brewery.client.render.RopeKnotRenderer;
import net.bmjo.brewery.client.render.WaterBasinRenderer;
import net.bmjo.brewery.client.render.model.RopeKnotEntityModel;
import net.bmjo.brewery.event.KeyInputHandler;
import net.bmjo.brewery.item.ItemPredicate;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.registry.ModelRegistry;
import net.bmjo.brewery.util.rope.RopeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public class BreweryClient {

    public static void onInitializeClient() {
        KeyInputHandler.register();
        BreweryNetworking.registerS2CPackets();
        ItemPredicate.register();
        registerRenderer();

        ClientTickEvent.CLIENT_LEVEL_PRE.register((clientLevel) -> RopeHelper.tick());
    }

    private static void registerRenderer() {
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), WaterBasinRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.HOP_ROPE_KNOT, RopeKnotRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.HANGING_ROPE, HangingRopeRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.ROPE_COLLISION, RopeCollisionEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), WaterBasinRenderer::new);

        EntityModelLayerRegistry.register(ModelRegistry.ROPE_KNOT, RopeKnotEntityModel::getTexturedModelData);
    }

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}
