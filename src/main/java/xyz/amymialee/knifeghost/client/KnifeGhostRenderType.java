package xyz.amymialee.knifeghost.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.knifeghost.KnifeGhost;

import java.util.function.Function;

public class KnifeGhostRenderType {
    public static Function<Identifier, RenderLayer> KNIFEGHOST = Util.memoize(KnifeGhostRenderType::renderTypeName);
    private static net.minecraft.client.gl.ShaderProgram knifeghostShader;

    private static @NotNull RenderLayer renderTypeName(Identifier locationIn) {
        return RenderLayer.of(KnifeGhost.MOD_ID, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, RenderLayer.MultiPhaseParameters.builder()
                .program(new RenderPhase.ShaderProgram(() -> knifeghostShader)).texture(new RenderPhase.Texture(locationIn, false, false))
                .transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY).lightmap(RenderLayer.ENABLE_LIGHTMAP).overlay(RenderLayer.ENABLE_OVERLAY_COLOR).build(true));
    }

    public static void loadShader(net.minecraft.client.gl.ShaderProgram shaderInstance) {
        knifeghostShader = shaderInstance;
    }
}