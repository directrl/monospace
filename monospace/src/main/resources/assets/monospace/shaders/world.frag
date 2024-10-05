#version 330

in vec2 outTexCoord;
out vec4 fragColor;

struct Material {
    vec4 diffuseColor;
};

uniform sampler2D texSampler;
uniform Material material;
uniform int selection;

void main() {
    fragColor = texture(texSampler, outTexCoord) + material.diffuseColor;

    if(selection > 0) {
        fragColor = vec4(fragColor.x * 1.5, fragColor.y * 1.5, fragColor.z * 1.5, fragColor.w);
    }
}
