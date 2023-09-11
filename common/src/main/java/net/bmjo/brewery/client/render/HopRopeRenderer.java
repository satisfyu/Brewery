package net.bmjo.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.bmjo.brewery.client.RopeHelper;
import net.bmjo.brewery.client.render.model.HopRopeModel;
import net.bmjo.brewery.util.UVRect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HopRopeRenderer {
    private static final float CHAIN_SCALE = 1f;
    private static final int MAX_SEGMENTS = 2048;

    public HopRopeRenderer() {
        super();
    }

    public void renderRope(VertexConsumer buffer, PoseStack matrices, Vector3f chainVec, int blockLight0, int blockLight1, int skyLight0, int skyLight1) {
        HopRopeModel model = buildModel(chainVec);
        model.render(buffer, matrices, blockLight0, blockLight1, skyLight0, skyLight1);
    }

    private HopRopeModel buildModel(Vector3f chainVec) {
        float desiredSegmentLength = 1f / 4; //Quality
        int initialCapacity = (int) (2f * chainVec.lengthSquared() / desiredSegmentLength);
        HopRopeModel.Builder builder = HopRopeModel.builder(initialCapacity);

        if (Double.isNaN(chainVec.x()) && Double.isNaN(chainVec.z())) {
            buildFaceVertical(builder, chainVec, 45, UVRect.DEFAULT_SIDE_A);
            buildFaceVertical(builder, chainVec, -45, UVRect.DEFAULT_SIDE_B);
        } else {
            buildFace(builder, chainVec, 45, UVRect.DEFAULT_SIDE_A);
            buildFace(builder, chainVec, -45, UVRect.DEFAULT_SIDE_B);
        }

        return builder.build();
    }

    private void buildFaceVertical(HopRopeModel.Builder builder, Vector3f v, float angle, UVRect uv) {
        float actualSegmentLength = 1f / 4; //Quality
        float chainWidth = (uv.x1() - uv.x0()) / 16 * CHAIN_SCALE;

        Vector3f normal = new Vector3f((float) Math.cos(Math.toRadians(angle)), 0, (float) Math.sin(Math.toRadians(angle)));
        normal.normalize(chainWidth);

        Vector3f vert00 = new Vector3f(-normal.x() / 2, 0, -normal.z() / 2), vert01 = new Vector3f(vert00);
        Vector3f vert10 = new Vector3f(-normal.x() / 2, 0, -normal.z() / 2), vert11 = new Vector3f(vert10);

        float uvv0 = 0, uvv1 = 0;
        boolean lastIter = false;
        for (int segment = 0; segment < MAX_SEGMENTS; segment++) {
            if (vert00.y() + actualSegmentLength >= v.y()) {
                lastIter = true;
                actualSegmentLength = v.y() - vert00.y();
            }

            vert10.add(0, actualSegmentLength, 0);
            vert11.add(0, actualSegmentLength, 0);

            uvv1 += actualSegmentLength / CHAIN_SCALE;

            builder.vertex(vert00).uv(uv.x0() / 16f, uvv0).next();
            builder.vertex(vert01).uv(uv.x1() / 16f, uvv0).next();
            builder.vertex(vert11).uv(uv.x1() / 16f, uvv1).next();
            builder.vertex(vert10).uv(uv.x0() / 16f, uvv1).next();

            if (lastIter) break;

            uvv0 = uvv1;

            vert00.set(vert10);
            vert01.set(vert11);
        }
    }

    private void buildFace(HopRopeModel.Builder builder, Vector3f v, float angle, UVRect uv) {
        float actualSegmentLength, desiredSegmentLength = 1f / 4; //QUALITY
        float distance = v.length(), distanceXZ = (float) Math.sqrt(Math.fma(v.x(), v.x(), v.z() * v.z()));
        // Original code used total distance between start and end instead of horizontal distance
        // That changed the look of chains when there was a big height difference, but it looks better.
        float wrongDistanceFactor = distance / distanceXZ;

        // 00, 01, 11, 11 refers to the X and Y position of the vertex.
        // 00 is the lower X and Y vertex. 10 Has the same y value as 00 but a higher x value.
        Vector3f vert00 = new Vector3f(), vert01 = new Vector3f(), vert11 = new Vector3f(), vert10 = new Vector3f();
        Vector3f normal = new Vector3f(), rotAxis = new Vector3f();

        float chainWidth = (uv.x1() - uv.x0()) / 16 * CHAIN_SCALE;
        //
        float uvv0, uvv1 = 0, gradient, x, y;
        Vector3f point0 = new Vector3f(), point1 = new Vector3f();
        Quaternionf rotator = new Quaternionf();

        // All of this setup can probably go, but I can't figure out
        // how to integrate it into the loop :shrug:
        point0.set(0, (float) RopeHelper.drip2(0, distance, v.y()), 0);
        gradient = (float) RopeHelper.drip2prime(0, distance, v.y());
        normal.set(-gradient, Math.abs(distanceXZ / distance), 0);
        normal.normalize();

        x = estimateDeltaX(desiredSegmentLength, gradient);
        gradient = (float) RopeHelper.drip2prime(x * wrongDistanceFactor, distance, v.y());
        y = (float) RopeHelper.drip2(x * wrongDistanceFactor, distance, v.y());
        point1.set(x, y, 0);

        rotAxis.set(point1.x() - point0.x(), point1.y() - point0.y(), point1.z() - point0.z());
        rotAxis.normalize();
        rotator.fromAxisAngleDeg(rotAxis, angle);


        normal.rotate(rotator);
        normal.normalize(chainWidth);
        vert10.set(point0.x() - normal.x() / 2, point0.y() - normal.y() / 2, point0.z() - normal.z() / 2);
        vert11.set(vert10);
        vert11.add(normal);


        actualSegmentLength = point0.distance(point1);

        // This is a pretty simple algorithm to convert the mathematical curve to a model.
        // It uses an incremental approach, adding segments until the end is reached.
        boolean lastIter = false;
        for (int segment = 0; segment < MAX_SEGMENTS; segment++) {
            rotAxis.set(point1.x() - point0.x(), point1.y() - point0.y(), point1.z() - point0.z());
            rotAxis.normalize();
            rotator = rotator.fromAxisAngleDeg(rotAxis, angle);

            // This normal is orthogonal to the face normal
            normal.set(-gradient, Math.abs(distanceXZ / distance), 0);
            normal.normalize();
            normal.rotate(rotator);
            normal.normalize(chainWidth);

            vert00.set(vert10);
            vert01.set(vert11);

            vert10.set(point1.x() - normal.x() / 2, point1.y() - normal.y() / 2, point1.z() - normal.z() / 2);
            vert11.set(vert10);
            vert11.add(normal);

            uvv0 = uvv1;
            uvv1 = uvv0 + actualSegmentLength / CHAIN_SCALE;

            builder.vertex(vert00).uv(uv.x0() / 16f, uvv0).next();
            builder.vertex(vert01).uv(uv.x1() / 16f, uvv0).next();
            builder.vertex(vert11).uv(uv.x1() / 16f, uvv1).next();
            builder.vertex(vert10).uv(uv.x0() / 16f, uvv1).next();

            if (lastIter) break;

            point0.set(point1);

            x += estimateDeltaX(desiredSegmentLength, gradient);
            if (x >= distanceXZ) {
                lastIter = true;
                x = distanceXZ;
            }

            gradient = (float) RopeHelper.drip2prime(x * wrongDistanceFactor, distance, v.y());
            y = (float) RopeHelper.drip2(x * wrongDistanceFactor, distance, v.y());
            point1.set(x, y, 0);

            actualSegmentLength = point0.distance(point1);
        }
    }

    private float estimateDeltaX(float s, float k) {
        return (float) (s / Math.sqrt(1 + k * k));
    }
}
