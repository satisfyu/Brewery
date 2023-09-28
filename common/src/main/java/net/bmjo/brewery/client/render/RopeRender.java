package net.bmjo.brewery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.bmjo.brewery.util.rope.RopeHelper;
import net.bmjo.brewery.client.model.RopeModel;
import net.bmjo.brewery.util.rope.UVCord;
import net.minecraft.world.phys.Vec3;

public class RopeRender { //TODO wenn gerade dann nur ein vertex //TODO keys
    private static final float SCALE = 1.0F;
    private static final float QUALITY = 4.0F;
    private static final int MAX_SEGMENTS = 2048;
    private static final Vec3 POSITIVE_Y = new Vec3(0.0F, 1.0F, 0.0F);
    private static final Vec3 NEGATIVE_Y = new Vec3(0.0F, -1.0F, 0.0F);

    public void render(final VertexConsumer vertexConsumer, final PoseStack poseStack, final Vec3 ropeVec, final int blockLight0, final int blockLight1, final int skyLight0, final int skyLight1) {
        RopeModel model = buildModel(ropeVec);
        model.render(vertexConsumer, poseStack, blockLight0, blockLight1, skyLight0, skyLight1);
    }

    private RopeModel buildModel(final Vec3 ropeVec) {
        float desiredSegmentLength = 1F / 4;
        int initialCapacity = (int) (2F * ropeVec.lengthSqr() / desiredSegmentLength);
        RopeModel.Builder builder = RopeModel.builder(initialCapacity);

        createModel(builder, ropeVec, 45, UVCord.DEFAULT_ROPE_H);
        createModel(builder, ropeVec, -45, UVCord.DEFAULT_ROPE_V);

        return builder.build();
    }

    private void createModel(final RopeModel.Builder builder, final Vec3 ropeVec, final int degrees, final UVCord uv) {
        float length = (float) ropeVec.length();
        Vec3 ropeNormal = ropeVec.normalize();
        Quaternion quaternion = new Vector3f(ropeNormal).rotationDegrees(degrees);
        Vector3f crossVec = ropeNormal.equals(POSITIVE_Y) || ropeNormal.equals(NEGATIVE_Y) ? Vector3f.XP.copy() : new Vector3f(ropeNormal.cross(POSITIVE_Y).normalize()); //plane vector
        crossVec.transform(quaternion); //rotate plane ? degrees
        crossVec.mul(((uv.x1() - uv.x0()) / 16.0F) * SCALE); //width
        crossVec.mul(0.5F); //to each side

        float uvStart, uvEnd = 0;
        double segmentLength = Math.min(length, 1.0F / QUALITY), actuallySegmentLength;
        Vector3f currentPos = Vector3f.ZERO.copy(), lastPos = new Vector3f();
        Vector3f segmentVector = new Vector3f(ropeNormal.multiply(segmentLength, segmentLength, segmentLength)), segmentPos = Vector3f.ZERO.copy();

        boolean lastIter = false;
        for (int segment = 0; segment < MAX_SEGMENTS; segment++) {
            lastPos.load(currentPos); //current to last
            segmentPos.add(segmentVector); // add step on top


            if (new Vec3(segmentPos).length() > length) {
                lastIter = true;
                segmentPos.set((float) ropeVec.x, (float) ropeVec.y, (float) ropeVec.z);
            }

            currentPos.load(segmentPos); // set currentPos to top
            currentPos.add(0.0F, (float) RopeHelper.getYHanging(new Vec3(segmentPos).length(), ropeVec), 0.0F);

            actuallySegmentLength = new Vec3(currentPos).distanceTo(new Vec3(lastPos));

            uvStart = uvEnd;
            uvEnd += (float) (actuallySegmentLength / SCALE);

            builder.vertex(lastPos.x() - crossVec.x(), lastPos.y() - crossVec.y(), lastPos.z() - crossVec.z()).uv(uv.x0() / 16.0F, uvStart).next();
            builder.vertex(lastPos.x() + crossVec.x(), lastPos.y() + crossVec.y(), lastPos.z() + crossVec.z()).uv(uv.x1() / 16.0F, uvStart).next();
            builder.vertex(currentPos.x() + crossVec.x(), currentPos.y() + crossVec.y(), currentPos.z() + crossVec.z()).uv(uv.x1() / 16.0F, uvEnd).next();
            builder.vertex(currentPos.x() - crossVec.x(), currentPos.y() - crossVec.y(), currentPos.z() - crossVec.z()).uv(uv.x0() / 16.0F, uvEnd).next();

            if (lastIter) break;
        }
    }
}
