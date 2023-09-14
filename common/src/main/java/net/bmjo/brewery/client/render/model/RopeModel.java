package net.bmjo.brewery.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.bmjo.brewery.Brewery;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public record RopeModel(float[] vertices, float[] uvs) {

    public static Builder builder(int initialCapacity) {
        return new Builder(initialCapacity);
    }

    public void render(VertexConsumer buffer, PoseStack poseStack, int bLight0, int bLight1, int sLight0, int sLight1) {
        Matrix4f modelMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();
        int count = vertices.length / 3;
        for (int i = 0; i < count; i++) {
            float f = (i % (count / 2f)) / (count / 2f);
            int blockLight = (int) Mth.lerp(f, (float) bLight0, (float) bLight1);
            int skyLight = (int) Mth.lerp(f, (float) sLight0, (float) sLight1);
            int light = LightTexture.pack(blockLight, skyLight);
            buffer
                    .vertex(modelMatrix, vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2])
                    .color(-1)
                    .uv(uvs[i * 2], uvs[i * 2 + 1])
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .normal(normalMatrix, 1, 0.35f, 0)
                    .endVertex();
        }
    }

    public static class Builder {
        private final List<Float> vertices;
        private final List<Float> uvs;
        private int size;

        public Builder(int initialCapacity) {
            vertices = new ArrayList<>(initialCapacity * 3);
            uvs = new ArrayList<>(initialCapacity * 2);
        }

        public Builder vertex(float x, float y, float z) {
            vertices.add(x);
            vertices.add(y);
            vertices.add(z);
            return this;
        }

        public Builder uv(float u, float v) {
            uvs.add(u);
            uvs.add(v);
            return this;
        }

        public void next() {
            size++;
        }

        public RopeModel build() {
            if (vertices.size() != size * 3) Brewery.LOGGER.error("Wrong count of vertices"); //TODO
            if (uvs.size() != size * 2) Brewery.LOGGER.error("Wrong count of uvs");

            return new RopeModel(toFloatArray(vertices), toFloatArray(uvs));
        }

        private float[] toFloatArray(List<Float> floats) {
            float[] array = new float[floats.size()];
            int i = 0;

            for (float f : floats) {
                array[i++] = f;
            }

            return array;
        }
    }
}