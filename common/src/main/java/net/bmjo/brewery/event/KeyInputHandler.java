package net.bmjo.brewery.event;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.bmjo.brewery.client.BreweryClient;
import net.bmjo.brewery.alcohol.AlcoholPlayer;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyInputHandler {
    public static final String KEY_CATEGORY_BREWERY = "key.category.brewery";
    public static final String KEY_DRINK = "key.brewery.drink";
    public static final String KEY_INFO = "key.brewery.info";

    public static final KeyMapping drink = new KeyMapping(
            KEY_DRINK,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            KEY_CATEGORY_BREWERY
    );

    public static final KeyMapping info = new KeyMapping(
            KEY_INFO,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY_BREWERY
    );

    public static void registerKeyInputs() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (drink.consumeClick()) {
                LocalPlayer player = BreweryClient.getPlayer();
                if (player instanceof AlcoholPlayer) {
                    NetworkManager.sendToServer(BreweryNetworking.DRINK_ALCOHOL_C2S_ID, BreweryNetworking.createPacketBuf());
                }
            }
            if (info.consumeClick()) {
                LocalPlayer player = BreweryClient.getPlayer();
                if (player instanceof AlcoholPlayer alcoholPlayer) {
                    player.displayClientMessage(Component.literal(alcoholPlayer.getAlcohol().toString()), false);
                }
            }
        });
    }

    public static void register() {
        KeyMappingRegistry.register(drink);
        KeyMappingRegistry.register(info);
        registerKeyInputs();
    }
}
