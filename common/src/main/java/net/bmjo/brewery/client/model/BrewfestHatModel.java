package net.bmjo.brewery.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.bmjo.brewery.util.BreweryIdentifier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class BrewfestHatModel<T extends Entity> extends EntityModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new BreweryIdentifier("brewfest_hat"), "main");

	private final ModelPart brewfest_hat;
	public BrewfestHatModel(ModelPart root) {
		this.brewfest_hat = root.getChild("brewfest_hat");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("brewfest_hat", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -4.0F, 4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(-1, 5).addBox(-12.1F, -1.0F, 3.9F, 8.2F, 1.0F, 8.2F, new CubeDeformation(0.0F))
				.texOffs(-14, 15).addBox(-15.0F, 0.0F, 1.0F, 14.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

		PartDefinition feather_r1 = bone.addOrReplaceChild("feather_r1", CubeListBuilder.create().texOffs(18, 21).addBox(-0.1F, -4.0F, -3.5F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9F, -1.0F, 8.5F, 0.0F, 0.0F, 0.3927F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		brewfest_hat.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}