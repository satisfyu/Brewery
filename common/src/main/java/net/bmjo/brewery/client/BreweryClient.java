package net.bmjo.brewery.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.bmjo.brewery.client.model.RopeKnotEntityModel;
import net.bmjo.brewery.client.render.*;
import net.bmjo.brewery.event.KeyInputHandler;
import net.bmjo.brewery.item.ItemPredicate;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.registry.*;
import net.bmjo.brewery.util.rope.RopeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class BreweryClient {

    public static void onInitializeClient() {
        KeyInputHandler.register();
        BreweryNetworking.registerS2CPackets();
        ItemPredicate.register();
        registerRenderer();
        registerModelLayers();

        RenderTypeRegistry.register(RenderType.cutout(),
                ObjectRegistry.BREWERY_WINDOW.get(), ObjectRegistry.BREWERY_DOOR.get(), ObjectRegistry.BREWERY_TRAPDOOR.get(),
                ObjectRegistry.WILD_HOPS.get(), ObjectRegistry.BEER_MUG.get(), ObjectRegistry.BEER_WHEAT.get(),
                ObjectRegistry.BEER_HOPS.get(), ObjectRegistry.BEER_BARLEY.get(), ObjectRegistry.BEER_CHORUS.get(),
                ObjectRegistry.BARLEY_CROP.get(), ObjectRegistry.CORN_CROP.get()

        );

        ClientTickEvent.CLIENT_LEVEL_PRE.register((clientLevel) -> RopeHelper.tick());
    }

    public static void preInitClient(){
        registerEntityModelLayers();
    }



    private static void registerRenderer() {
        EntityModelLayerRegistry.register(ModelRegistry.ROPE_KNOT, RopeKnotEntityModel::createBodyLayer);
        EntityRendererRegistry.register(EntityRegistry.ROPE_KNOT, RopeKnotRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.HANGING_ROPE, HangingRopeRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.ROPE_COLLISION, RopeCollisionEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), WaterBasinRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BREW_KETTLE_BLOCK_ENTITY.get(), WaterBasinRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.STANDARD.get(), StandardRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntityRegistry.BEER_MUG_FLOWER_POT_BLOCK_ENTITY.get(), BeerKegBlockEntityRenderer::new);
    }


    public static void registerModelLayers() {
            EntityModelLayerRegistry.register(StandardRenderer.LAYER_LOCATION, StandardRenderer::createBodyLayer);
    }

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void registerEntityModelLayers(){
        CustomArmorRegistry.registerCustomArmorLayers();
    }
}
