package xyz.amymialee.knifeghost.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.knifeghost.entity.KnifeEntity;

public class KnifeEntityRenderer extends EntityRenderer<KnifeEntity> {
    private final ItemRenderer itemRenderer;

    public KnifeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(@NotNull KnifeEntity knife, float yaw, float tickDelta, @NotNull MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(1.45f, 1.45f, 1.45f);
        var camera = MinecraftClient.getInstance().cameraEntity;
        if (camera == knife.getOwner() && knife.age < 20) {
            var pos = knife.getPos().lerp(new Vec3d(knife.prevX, knife.prevY, knife.prevZ), 1 - tickDelta);
            if (pos.distanceTo(MinecraftClient.getInstance().cameraEntity.getEyePos()) < 2.4) {
                var dist = pos.distanceTo(MinecraftClient.getInstance().cameraEntity.getEyePos()) / 2.4;
                matrices.scale((float) dist, (float) dist, (float) dist);
            }
        }
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-MathHelper.lerp(tickDelta, knife.prevPitch, knife.getPitch())));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90 + MathHelper.lerp(tickDelta, knife.prevYaw, knife.getYaw())));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(tickDelta, knife.prevRoll, knife.roll)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
        this.itemRenderer.renderItem(knife.getStack(), ModelTransformationMode.FIXED, 255, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, knife.getWorld(), knife.getId());
        matrices.pop();
        super.render(knife, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(KnifeEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}