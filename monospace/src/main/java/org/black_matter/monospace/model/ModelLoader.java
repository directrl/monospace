package org.black_matter.monospace.model;

import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.object.collision.AABB;
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
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {
	
	private ModelLoader() { }
	
	public static Model load(Resource modelResource, int flags) {
		var cachedModel = Cache.MODELS.get(modelResource.name);
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
		
		try(var scene = aiImportFileFromMemory(modelBuffer, flags, (CharSequence) null)) {
			var model = new Model(modelResource.name, null, null);
			
			if(scene == null) {
				Monospace.LOGGER.error("Could not load model",
					new LoadingException(modelResource.getPath()));
				return null;
			}
			
			List<Material> materials = new ArrayList<>();
			
			for(int i = 0; i < scene.mNumMaterials(); i++) {
				var aiMaterial = AIMaterial.create(scene.mMaterials().get(i));
				materials.add(Material.createForModel(scene, aiMaterial, model));
			}
			
			PointerBuffer aiMeshes = scene.mMeshes();
			List<Mesh> meshes = new ArrayList<>();
			List<AABB> aabbs = new ArrayList<>();
			
			for(int i = 0; i < scene.mNumMeshes(); i++) {
				var aiMesh = AIMesh.create(aiMeshes.get(i));
				var mesh = Mesh.createForModel(aiMesh, model);
				meshes.add(mesh);
				
				var aiAabb = aiMesh.mAABB();
				var aabb = new AABB(
					aiAabb.mMin().x(), aiAabb.mMin().y(), aiAabb.mMin().z(),
					aiAabb.mMax().x(), aiAabb.mMax().y(), aiAabb.mMax().z()
				);
				aabbs.add(aabb);
			}
			
			Material defaultMaterial = new Material();
			
			for(int i = 0; i < scene.mNumMeshes(); i++) {
				var aiMesh = AIMesh.create(aiMeshes.get(i));
				int materialIndex = aiMesh.mMaterialIndex();
				Material material;
				
				if(materialIndex >= 0 && materialIndex < materials.size()) {
					material = materials.get(materialIndex);
				} else {
					material = defaultMaterial;
				}
				
				material.setMeshes(meshes);
			}
			
			if(!defaultMaterial.getMeshes().isEmpty()) {
				materials.add(defaultMaterial);
			}
			
			model.materials = materials;
			model.aabb = aabbs;
			
			Monospace.LOGGER.trace("Caching model " + model);
			Cache.MODELS.put(modelResource.name, model);
			return model;
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
			| aiProcess_PreTransformVertices
			| aiProcess_GenBoundingBoxes);
	}
	
	private static class Cache {
		
		private static final Map<String, Model> MODELS = new HashMap<>();
		
		public static void close() {
			MODELS.values().forEach(Model::close);
		}
	}
	
	public static class LoadingException extends Exception {
		
		public LoadingException(String modelPath) {
			super("Error occurred while loading model " + modelPath + ": " + aiGetErrorString());
		}
	}
}
