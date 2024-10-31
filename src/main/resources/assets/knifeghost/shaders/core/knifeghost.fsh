#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;

out vec4 fragColor;

const float BAYER_MATRIX[64] = float[64](
1, 49, 13, 61, 4, 52, 16, 64,
33, 17, 45, 29, 36, 20, 48, 32,
9, 57, 5, 53, 12, 60, 8, 56,
41, 25, 37, 21, 44, 28, 40, 24,
3, 51, 15, 63, 2, 50, 14, 62,
35, 19, 47, 31, 34, 18, 46, 30,
11, 59, 7, 55, 10, 58, 6, 54,
43, 27, 39, 23, 42, 26, 38, 22
);

void main() {
    ivec2 pos = ivec2(gl_FragCoord.xy) % 8;
    int index = pos.y * 8 + pos.x;
    float threshold = (BAYER_MATRIX[index] - 0.5) / 64.0;
    if (vertexColor.a < threshold) {
        discard;
    }
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
