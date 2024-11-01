package xyz.amymialee.knifeghost;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.tick.TickManager;
import xyz.amymialee.knifeghost.client.KnifeEntityRenderer;
import xyz.amymialee.knifeghost.client.KnifeGhostEntityModel;
import xyz.amymialee.knifeghost.client.KnifeGhostEntityRenderer;
import xyz.amymialee.knifeghost.client.KnifeGhostRenderType;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;

import java.util.ArrayList;
import java.util.List;

public class KnifeGhostClient implements ClientModInitializer {
    public static final EntityModelLayer KNIFE_GHOST_LAYER = new EntityModelLayer(KnifeGhost.id("knifeghost"), "main");
    public static final List<KnifeGhostEntity> GHOST_RENDERS = new ArrayList<>();
    public static boolean isLast = false;

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(KNIFE_GHOST_LAYER, KnifeGhostEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(KnifeGhost.KNIFE_GHOST_ENTITY, KnifeGhostEntityRenderer::new);
        EntityRendererRegistry.register(KnifeGhost.KNIFE_ENTITY, KnifeEntityRenderer::new);
        CoreShaderRegistrationCallback.EVENT.register((callback) -> callback.register(KnifeGhost.id(KnifeGhost.MOD_ID), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, KnifeGhostRenderType::loadShader));
        WorldRenderEvents.LAST.register((context) -> {
            isLast = true;
            rendering: {
                var camera = context.camera();
                if (camera == null) break rendering;
                var client = MinecraftClient.getInstance();
                var cameraPos = camera.getPos();
                var immediate = client.getBufferBuilders().getEntityVertexConsumers();
                var tickCounter = client.getRenderTickCounter();
                var delta = tickCounter.getTickDelta(true);
                for (var entity : GHOST_RENDERS) {
                    var pos = entity.getBlockPos();
                    if ((!context.world().isOutOfHeightLimit(pos.getY()) && !context.worldRenderer().isRenderingReady(pos)) || (entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity) camera.getFocusedEntity()).isSleeping()))) continue;
                    if (entity.age == 0) {
                        entity.lastRenderX = entity.getX();
                        entity.lastRenderY = entity.getY();
                        entity.lastRenderZ = entity.getZ();
                    }
                    var d = MathHelper.lerp(delta, entity.lastRenderX, entity.getX());
                    var e = MathHelper.lerp(delta, entity.lastRenderY, entity.getY());
                    var f = MathHelper.lerp(delta, entity.lastRenderZ, entity.getZ());
                    var g = MathHelper.lerp(delta, entity.prevYaw, entity.getYaw());
                    VertexConsumerProvider provider = immediate;
                    if (client.worldRenderer.canDrawEntityOutlines() && client.hasOutline(entity)) {
                        var outlineProvider = client.getBufferBuilders().getOutlineVertexConsumers();
                        var color = entity.getTeamColorValue();
                        outlineProvider.setColor(ColorHelper.Argb.getRed(color), ColorHelper.Argb.getGreen(color), ColorHelper.Argb.getBlue(color), 255);
                        provider = outlineProvider;
                    }
                    context.worldRenderer().entityRenderDispatcher.render(entity, d - cameraPos.getX(), e - cameraPos.getY(), f - cameraPos.getZ(), g, delta, context.matrixStack(), provider, 255);
                }
            }
            GHOST_RENDERS.clear();
            isLast = false;
        });
    }
}