package net.bmjo.brewery.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.bmjo.brewery.client.render.*;
import net.bmjo.brewery.client.render.model.RopeKnotEntityModel;
import net.bmjo.brewery.event.KeyInputHandler;
import net.bmjo.brewery.item.ItemPredicate;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.BlockEntityRegistry;
import net.bmjo.brewery.registry.EntityRegistry;
import net.bmjo.brewery.registry.ModelRegistry;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.bmjo.brewery.util.rope.RopeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
public class BreweryClient {

    public static void onInitializeClient() {
        KeyInputHandler.register();
        BreweryNetworking.registerS2CPackets();
        ItemPredicate.register();
        registerRenderer();
        registerModelLayers();

        ClientTickEvent.CLIENT_LEVEL_PRE.register((clientLevel) -> RopeHelper.tick());

        ClientGuiEvent.RENDER_HUD.register((poseStack, f) -> {
            poseStack.pushPose();
            poseStack.translate(40, 40, 0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees((System.currentTimeMillis() % 1000) / 1000f * 360f));
            poseStack.translate(-40, -40, 0);
            Matrix4f positionMatrix = poseStack.last().pose();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            buffer.vertex(positionMatrix, 20, 20, 0).color(1f, 1f, 1f, 1f).uv(0f, 0f).endVertex();
            buffer.vertex(positionMatrix, 20, 60, 0).color(1f, 0f, 0f, 1f).uv(0f, 1f).endVertex();
            buffer.vertex(positionMatrix, 60, 60, 0).color(0f, 0f, 1f, 1f).uv(1f, 1f).endVertex();
            buffer.vertex(positionMatrix, 60, 20, 0).color(0f, 1f, 0f, 1f).uv(1f, 0f).endVertex();

            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            RenderSystem.setShaderTexture(0, new BreweryIdentifier("icon.png"));
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);


            tesselator.end();
            poseStack.popPose();
        });
    }


    private static void registerRenderer() {
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), WaterBasinRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.HOP_ROPE_KNOT, RopeKnotRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.ROPE_COLLISION, RopeCollisionEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), WaterBasinRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.STANDARD.get(), StandardRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BEER_KEG_FLOWER_POT_BLOCK_ENTITY.get(), BeerKegBlockEntityRenderer::new);

        EntityModelLayerRegistry.register(ModelRegistry.CHAIN_KNOT, RopeKnotEntityModel::getTexturedModelData);
    }

    public static void registerModelLayers() {
            EntityModelLayerRegistry.register(StandardRenderer.LAYER_LOCATION, StandardRenderer::createBodyLayer);
    }

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}
