package net.bmjo.brewery.client;

import net.bmjo.brewery.event.KeyInputHandler;
import net.bmjo.brewery.networking.BreweryNetworking;
import net.bmjo.brewery.item.ItemPredicate;
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
    }

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}
