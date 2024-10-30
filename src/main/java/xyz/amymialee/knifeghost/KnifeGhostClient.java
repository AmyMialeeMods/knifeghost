package xyz.amymialee.knifeghost;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import xyz.amymialee.knifeghost.client.KnifeEntityRenderer;
import xyz.amymialee.knifeghost.client.KnifeGhostEntityModel;
import xyz.amymialee.knifeghost.client.KnifeGhostEntityRenderer;

public class KnifeGhostClient implements ClientModInitializer {
    public static final EntityModelLayer KNIFE_GHOST_LAYER = new EntityModelLayer(KnifeGhost.id("knifeghost"), "main");

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(KNIFE_GHOST_LAYER, KnifeGhostEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(KnifeGhost.KNIFE_GHOST_ENTITY, KnifeGhostEntityRenderer::new);
        EntityRendererRegistry.register(KnifeGhost.KNIFE_ENTITY, KnifeEntityRenderer::new);
    }
}