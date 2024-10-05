package org.black_matter.monospace.render;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.render.gl.ShaderProgram;
import org.black_matter.monospace.util.Resource;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Material implements Closeable {
	
	@Getter @Setter private List<Mesh> meshes;
	@Getter @Setter private Texture texture;
	@Getter @Setter private Vector4f diffuseColor;
	
	public Material() {
		this.meshes = new ArrayList<>();
		this.diffuseColor = new Vector4f(0, 0, 0, 1);
	}
	
	public Material(Texture texture, List<Mesh> meshes) {
		this.texture = texture;
		this.meshes = meshes;
		this.diffuseColor = new Vector4f(0, 0, 0, 1);
	}
	
	public static Material createForModel(AIScene aiScene, AIMaterial aiMaterial, Model model) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			var material = new Material();
			
			var color = AIColor4D.create();
			int res = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
			if(res == aiReturn_SUCCESS) {
				// something is broken idk we just assume its broken
				if(color.r() != 1 && color.g() != 1 && color.b() != 1 && color.a() != 1) {
					material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
				}
			}
			
			var aiTexturePath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath,
				(IntBuffer) null, null, null, null, null, null);
			var texturePath = aiTexturePath.dataString();
			
			Texture texture = null;
			
		tex:
			if(!texturePath.isEmpty() && !texturePath.startsWith("*")) {
				// TODO load external texture
				Monospace.LOGGER.warn(texturePath);
				assert false;
			} else if(aiScene.mNumTextures() > 0) {
				// if we are here, it means that the texture is embedded inside the model
				// assimp seems to prefix embedded texture paths (ids) with *, so we need to remove that
				
				texturePath = texturePath.replace("*", "");
				
				if(texturePath.isEmpty()) {
					texturePath = "0";
					//break tex;
				}
				
				var aiTextures = aiScene.mTextures();
				var aiTexturePointer = aiTextures.get(Integer.parseInt(texturePath));
				
				if(aiTexturePointer == MemoryUtil.NULL) {
					break tex;
				}
				
				var aiTexture = AITexture.create(aiTexturePointer);
				texture = Texture.create(new Resource(
					Resource.Type.CUSTOM,
					model.getId() + "/" + texturePath,
					null
				), aiTexture);
			}
			
		ret:
			if(texture == null) {
				material.setTexture(Texture.DEFAULT_TEXTURE);
			} else {
				material.setTexture(texture);
			}
			
			return material;
		}
	}
	
	@Override
	public void close() {
		meshes.forEach(Mesh::close);
	}
}
