package xyz.amymialee.knifeghost.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;

public class KnifeGhostEntityModel extends EntityModel<KnifeGhostEntity> {
    private final ModelPart head;
    private final ModelPart dangle;
    private final ModelPart body;

    public KnifeGhostEntityModel(@NotNull ModelPart root) {
        this.head = root.getChild("head");
        this.dangle = this.head.getChild("dangle");
        this.body = root.getChild("body");
    }

    public static @NotNull TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var modelPartData = modelData.getRoot();
        var head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 33).cuboid(-7.0F, -13.0F, -7.0F, 14.0F, 13.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));
        head.addChild("dangle", ModelPartBuilder.create().uv(56, 0).cuboid(-3.0F, -2.0F, 0.0F, 6.0F, 4.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -11.0F, 7.0F, -0.7854F, 0.0F, 0.0F));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, -22.0F, -7.0F, 14.0F, 19.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(@NotNull KnifeGhostEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        var h = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        this.head.yaw = netHeadYaw * 0.017453292F;
        if (entity.getLeaningPitch(h) > 0.0F) {
            this.head.pitch = this.lerpAngle(entity.getLeaningPitch(h), this.head.pitch, headPitch * 0.017453292F);
        } else {
            this.head.pitch = headPitch * 0.017453292F;
        }
        var d = MathHelper.lerp(h, entity.prevCapeX, entity.capeX) - MathHelper.lerp(h, entity.prevX, entity.getX());
        var e = MathHelper.lerp(h, entity.prevCapeY, entity.capeY) - MathHelper.lerp(h, entity.prevY, entity.getY());
        var m = MathHelper.lerp(h, entity.prevCapeZ, entity.capeZ) - MathHelper.lerp(h, entity.prevZ, entity.getZ());
        var n = entity.prevBodyYaw + (entity.bodyYaw - entity.prevBodyYaw);
        var o = (double)MathHelper.sin(n * 0.017453292F);
        var p = (double)(-MathHelper.cos(n * 0.017453292F));
        var q = (float)e * 10.0F;
        q = MathHelper.clamp(q, -6.0F, 32.0F);
        var r = (float)(d * o + m * p) * 100.0F;
        r = MathHelper.clamp(r, 0.0F, 150.0F);
        var s = (float)(d * p - m * o) * 100.0F;
        s = MathHelper.clamp(s, -20.0F, 20.0F);
        if (r < 0.0F) r = 0.0F;
        var t = MathHelper.lerp(h, entity.prevStrideDistance, entity.strideDistance);
        q += MathHelper.sin(MathHelper.lerp(h, entity.prevHorizontalSpeed, entity.horizontalSpeed) * 6.0F) * 32.0F * t;
        this.dangle.pitch = (float) (MathHelper.sqrt((float) Math.toRadians(6.0F + r / 2.0F + q /2)) * 2f - Math.toRadians(100));
        this.dangle.roll = (float) Math.toRadians(s / 2.0F);
        this.dangle.yaw = (float) Math.toRadians(s / 2.0F);;
    }

    protected float lerpAngle(float angleOne, float angleTwo, float magnitude) {
        var f = (magnitude - angleTwo) % 6.2831855F;
        if (f < -3.1415927F) f += 6.2831855F;
        if (f >= 3.1415927F) f -= 6.2831855F;
        return angleTwo + angleOne * f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.head.render(matrices, vertices, light, overlay, color);
        this.body.render(matrices, vertices, light, overlay, color);
    }
}