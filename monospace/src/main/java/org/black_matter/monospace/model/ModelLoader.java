package org.black_matter.monospace.model;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.render.Material;
import org.black_matter.monospace.render.Mesh;
import org.black_matter.monospace.render.Texture;
import org.black_matter.monospace.util.Resource;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIString;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;

public class ModelLoader {
	
	private ModelLoader() { }
	
	public static Model load(Resource modelResource, int flags) {
		var cachedModel = Cache.get(modelResource);
		if(cachedModel != null) return cachedModel;
		
		byte[] modelData;
		
		try {
			modelData = modelResource.readAllBytes();
		} catch(Exception e) {
			Monospace.LOGGER.error("Could not load model",
				new LoadingException(modelResource.getPath()));
			return null;
		}
		
		var modelBuffer = ByteBuffer.allocateDirect(modelData.length).order(ByteOrder.nativeOrder());
		modelBuffer.put(modelData);
		modelBuffer.position(0);
		
		try(var scene = aiImportFile(modelBuffer, flags)) {
			if(scene == null) {
				Monospace.LOGGER.error("Could not load model",
					new LoadingException(modelResource.getPath()));
				return null;
			}
			
			List<Material> materials = new ArrayList<>();
			
			for(int i = 0; i < scene.mNumMaterials(); i++) {
				var aiMaterial = AIMaterial.create(scene.mMaterials().get(i));
				materials.add(Material.create(aiMaterial, modelResource.getName()));
			}
			
			PointerBuffer aiMeshes = scene.mMeshes();
			Material defaultMaterial = new Material();
			
			for(int i = 0; i < scene.mNumMeshes(); i++) {
				var aiMesh = AIMesh.create(aiMeshes.get(i));
				var mesh = Mesh.create(aiMesh);
				
				int materialIndex = aiMesh.mMaterialIndex();
				Material material;
				
				if(materialIndex >= 0 && materialIndex < materials.size()) {
					material = materials.get(materialIndex);
				} else {
					material = defaultMaterial;
				}
				
				material.getMeshes().add(mesh);
			}
			
			if(!defaultMaterial.getMeshes().isEmpty()) {
				materials.add(defaultMaterial);
			}
			
			return new Model(modelResource.name, materials);
		}
	}
	
	public static Model load(Resource modelResource) {
		return load(modelResource,
			aiProcess_GenSmoothNormals
			| aiProcess_JoinIdenticalVertices
			| aiProcess_Triangulate
			| aiProcess_FixInfacingNormals
			| aiProcess_CalcTangentSpace
			| aiProcess_LimitBoneWeights
			| aiProcess_PreTransformVertices);
	}
	
	private static class Cache {
		
		private static final Map<String, Model> models = new HashMap<>();
		
		public static Model get(Resource resource) {
			if(models.containsKey(resource.name)) {
				return models.get(resource.name);
			}
			
			return null;
		}
		
		public static void set(Resource resource, Model model) {
			models.put(resource.name, model);
		}
		
		public static void remove(Resource resource) {
			models.remove(resource.name);
		}
		
		public static void close() {
			models.values().forEach(Model::close);
		}
	}
	
	public static class LoadingException extends Exception {
		
		public LoadingException(String modelPath) {
			super("Error occurred while loading model " + modelPath + ": " + aiGetErrorString());
		}
	}
}
