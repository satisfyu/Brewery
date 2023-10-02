package net.bmjo.brewery.entity.beer_elemental;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

import static net.bmjo.brewery.Brewery.MOD_ID;

public class BeerElementalModel<T extends Blaze> extends EntityModel<T> {
    public static final ModelLayerLocation BEER_ELEMENTAL_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(MOD_ID, "beer_elemental"), "main");
    private final ModelPart upperBodyParts0;
    private final ModelPart upperBodyParts1;
    private final ModelPart upperBodyParts2;
    private final ModelPart upperBodyParts3;
    private final ModelPart upperBodyParts4;
    private final ModelPart upperBodyParts5;
    private final ModelPart upperBodyParts6;
    private final ModelPart upperBodyParts7;
    private final ModelPart upperBodyParts8;
    private final ModelPart upperBodyParts9;
    private final ModelPart upperBodyParts10;
    private final ModelPart upperBodyParts11;
    private final ModelPart Head;

    public BeerElementalModel(ModelPart root) {
        this.upperBodyParts0 = root.getChild("upperBodyParts0");
        this.upperBodyParts1 = root.getChild("upperBodyParts1");
        this.upperBodyParts2 = root.getChild("upperBodyParts2");
        this.upperBodyParts3 = root.getChild("upperBodyParts3");
        this.upperBodyParts4 = root.getChild("upperBodyParts4");
        this.upperBodyParts5 = root.getChild("upperBodyParts5");
        this.upperBodyParts6 = root.getChild("upperBodyParts6");
        this.upperBodyParts7 = root.getChild("upperBodyParts7");
        this.upperBodyParts8 = root.getChild("upperBodyParts8");
        this.upperBodyParts9 = root.getChild("upperBodyParts9");
        this.upperBodyParts10 = root.getChild("upperBodyParts10");
        this.upperBodyParts11 = root.getChild("upperBodyParts11");
        this.Head = root.getChild("Head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition upperBodyParts0 = partdefinition.addOrReplaceChild("upperBodyParts0", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 0.0F, 3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 0.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 21).addBox(-1.0F, 7.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(4.0F, 1.0F, 1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -2.0F, -3.0F));

        PartDefinition upperBodyParts1 = partdefinition.addOrReplaceChild("upperBodyParts1", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 0.0F, 3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 0.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 21).addBox(-1.0F, 7.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).mirror().addBox(-5.0F, 1.0F, 1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-10.0F, -2.0F, 1.0F));

        PartDefinition upperBodyParts2 = partdefinition.addOrReplaceChild("upperBodyParts2", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 0.0F, 3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 0.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 0.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 21).addBox(-1.0F, 7.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).mirror().addBox(-5.0F, 1.0F, 1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, -2.0F, 8.0F));

        PartDefinition upperBodyParts3 = partdefinition.addOrReplaceChild("upperBodyParts3", CubeListBuilder.create(), PartPose.offset(-3.0F, -2.0F, -10.0F));

        PartDefinition upperBodyParts4 = partdefinition.addOrReplaceChild("upperBodyParts4", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, 2.0F, 1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(28, 21).addBox(-1.0F, 8.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 1.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 1.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 1.0F, 3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-2.0F, 1.0F, -2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 6.0F, -1.0F));

        PartDefinition upperBodyParts5 = partdefinition.addOrReplaceChild("upperBodyParts5", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, 2.0F, 1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(28, 21).addBox(-1.0F, 8.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 1.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 1.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 1.0F, 3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-2.0F, 1.0F, -2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 6.0F, -1.0F));

        PartDefinition upperBodyParts6 = partdefinition.addOrReplaceChild("upperBodyParts6", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, 2.0F, 1.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(28, 21).addBox(-1.0F, 8.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 1.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 1.0F, -1.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 1.0F, 3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-2.0F, 1.0F, -2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 6.0F, 5.0F));

        PartDefinition upperBodyParts7 = partdefinition.addOrReplaceChild("upperBodyParts7", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-5.0F, 2.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(28, 21).addBox(-1.0F, 8.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(3.0F, 1.0F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-2.0F, 1.0F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(-2.0F, 1.0F, 2.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-2.0F, 1.0F, -3.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 6.0F, -7.0F));

        PartDefinition upperBodyParts8 = partdefinition.addOrReplaceChild("upperBodyParts8", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 16.0F, 2.0F));

        PartDefinition upperBodyParts9 = partdefinition.addOrReplaceChild("upperBodyParts9", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 16.0F, -4.0F));

        PartDefinition upperBodyParts10 = partdefinition.addOrReplaceChild("upperBodyParts10", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 16.0F, 3.0F));

        PartDefinition upperBodyParts11 = partdefinition.addOrReplaceChild("upperBodyParts11", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 16.0F, -5.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0873F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        upperBodyParts0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts5.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts6.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts7.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts8.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts9.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts10.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        upperBodyParts11.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}