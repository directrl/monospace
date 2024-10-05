package org.black_matter.monospace.render;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.util.Resource;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.system.MemoryStack;

import java.io.Closeable;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;

public class Material implements Closeable {
	
	@Getter private final List<Mesh> meshes;
	@Getter @Setter private Texture texture;
	@Getter @Setter private Vector4f diffuseColor;
	
	public Material() {
		this.meshes = new ArrayList<>();
		this.diffuseColor = new Vector4f(0, 0, 0, 1);
	}
	
	public Material(Texture texture, List<Mesh> meshes) {
		this.texture = texture;
		this.meshes = meshes;
	}
	
	public static Material create(AIMaterial aiMaterial, String textureDirectory) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			var material = new Material();
			
			var color = AIColor4D.create();
			int res = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
			if(res == aiReturn_SUCCESS) {
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			
			var aiTexturePath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath,
				(IntBuffer) null, null, null, null, null, null);
			var texturePath = aiTexturePath.dataString();
			
			if(texturePath != null && !texturePath.isEmpty()) {
				// TODO
				System.out.println(textureDirectory + "/" + texturePath);
				material.setTexture(Texture.create(new Resource(
					Resource.Type.CUSTOM,
					texturePath,
					"models/" + textureDirectory + "/" + texturePath
				)));
				material.setDiffuseColor(new Vector4f(0, 0, 0, 1));
			}
			
			return material;
		}
	}
	
	@Override
	public void close() {
		meshes.forEach(Mesh::close);
	}
}
