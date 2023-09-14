package net.bmjo.brewery.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class RopeKnotEntityModel<T extends Entity> extends HierarchicalModel<T> { //TODO
    private final ModelPart chainKnot;

    public RopeKnotEntityModel(ModelPart root) {
        this.chainKnot = root.getChild("knot");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition bb_main = modelPartData.addOrReplaceChild("knot", CubeListBuilder.create(), PartPose.offset(0.0F, -12.5F, 0.0F));

        bb_main.addOrReplaceChild("knot_child", CubeListBuilder.create().texOffs(3, 1).addBox(-1.0F, -1.5F, 3.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 1).addBox(-1.0F, -1.5F, -3.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).mirror().addBox(-1.0F, 4.5F, -3.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(3, 6).addBox(-1.0F, -1.5F, -3.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 7.0F, 0.0F, 0.0F, 0.0F, -1.5708F));
        return LayerDefinition.create(modelData, 16, 16);
    }

    @Override
    public @NotNull ModelPart root() {
        return chainKnot;
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }
}