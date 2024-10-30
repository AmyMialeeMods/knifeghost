package xyz.amymialee.knifeghost.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.knifeghost.KnifeGhost;
import xyz.amymialee.knifeghost.KnifeGhostClient;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;

public class KnifeGhostEntityRenderer extends MobEntityRenderer<KnifeGhostEntity, KnifeGhostEntityModel> {
    private static final Identifier TEXTURE = KnifeGhost.id("textures/entity/knifeghost.png");

    public KnifeGhostEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new KnifeGhostEntityModel(context.getPart(KnifeGhostClient.KNIFE_GHOST_LAYER)), 0.5F);
    }

    @Override
    public void render(KnifeGhostEntity ghost, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int i) {
        super.render(ghost, f, g, matrixStack, vertexConsumers, i);
        var client = MinecraftClient.getInstance();
        matrixStack.push();
        var x = MathHelper.lerp(g, ghost.prevX, ghost.getX());
        var y = MathHelper.lerp(g, ghost.prevY, ghost.getY());
        var z = MathHelper.lerp(g, ghost.prevZ, ghost.getZ());
        matrixStack.translate(-x, -y, -z);
        for (var knife = 0; knife < KnifeGhostEntity.KNIFE_COUNT; knife++) {
            if (!ghost.hasKnife(knife)) continue;
            matrixStack.push();
            var prevPos = ghost.knifePrevPos.get(knife);
            var pos = ghost.knifePos.get(knife);
            var dx = MathHelper.lerp(g, prevPos.getX(), pos.getX());
            var dy = MathHelper.lerp(g, prevPos.getY(), pos.getY());
            var dz = MathHelper.lerp(g, prevPos.getZ(), pos.getZ());
            var knifeYaw = MathHelper.lerpAngleDegrees(g, ghost.knifePrevYaw.get(knife), ghost.knifeYaw.get(knife));
            var knifePitch = MathHelper.lerpAngleDegrees(g, ghost.knifePrevPitch.get(knife), ghost.knifePitch.get(knife));
            var knifeRoll = MathHelper.lerpAngleDegrees(g, ghost.knifePrevRoll.get(knife), ghost.knifeRoll.get(knife));
            matrixStack.translate(dx, dy, dz);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(knifeYaw));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(knifePitch));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(knifeRoll));
            client.getItemRenderer().renderItem(ghost.knives.getKnifeStack(knife), ModelTransformationMode.FIXED, 255, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumers, ghost.getWorld(), ghost.getId());
            matrixStack.pop();
        }
        matrixStack.pop();

//        var age = ghost.age + g;
//        for (var knife = 0; knife < KnifeGhostEntity.KNIFE_COUNT; knife++) {
//            matrixStack.push();
//            matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
//            matrixStack.translate(x, 1.35 + frontage / 2 + Math.sin(Math.pow(knife + 4, 4) + age * 0.3) * 0.08, z);
//            matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(yaw));
//            matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(-22.5f));
//            client.getItemRenderer().renderItem(ghost.knives.getKnifeStack(knife), ModelTransformationMode.FIXED, 255, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumers, ghost.getWorld(), ghost.getId());
//            matrixStack.pop();
//        }
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(KnifeGhostEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return RenderLayer.getEntityTranslucentCull(this.getTexture(entity));
    }

    @Override
    public Identifier getTexture(KnifeGhostEntity blazeEntity) {
        return TEXTURE;
    }
}