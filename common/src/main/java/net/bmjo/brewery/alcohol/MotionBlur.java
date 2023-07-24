package net.bmjo.brewery.alcohol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class MotionBlur {
    public static Minecraft client = Minecraft.getInstance();
    public static PostChain shader;
    public static boolean enabled = false;

    public static void activate() {
        if (shader != null) return;
        shader = getShader();
        if (shader != null) {
            shader.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
            enabled = true;
        } else {
            enabled = false;
        }
    }

    public static void deactivate() {
        if (shader != null) {
            shader.close();
        }
        shader = null;
        enabled = false;
    }

    public static PostChain getShader() {
        try {
            return new PostChain(client.getTextureManager(), client.getResourceManager(), client.getMainRenderTarget(), new ResourceLocation("shaders/post/phosphor.json"));
        } catch (IOException e) {
            return null;
        }
    }
}
