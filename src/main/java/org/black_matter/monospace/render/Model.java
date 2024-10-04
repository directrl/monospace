package org.black_matter.monospace.render;

import lombok.Getter;
import lombok.SneakyThrows;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.util.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model implements Closeable {
	
	@Getter private final String id;
	//@Getter private final List<Mesh> meshes;
	@Getter private final List<Material> materials;
	
	public Model(String id, /*List<Mesh> meshes,*/ List<Material> materials) {
		this.id = id;
		//this.meshes = meshes;
		this.materials = materials;
	}
	
	public void render() {
		//meshes.forEach(Mesh::render);
		materials.forEach(material -> {
			GL30.glActiveTexture(GL30.GL_TEXTURE0);
			material.getTexture().bind();
			material.getMeshes().forEach(Mesh::render);
		});
	}
	
	@SneakyThrows
	@Override
	public void close() {
		//meshes.forEach(Mesh::close);
		materials.forEach(Material::close);
	}
	
	public static class Registry {
		
		public static final Map<String, Model> MODELS = new HashMap<>();
		
		static {
			float[] positions = new float[] {
				// V0
				-0.5f, 0.5f, 0.5f,
				// V1
				-0.5f, -0.5f, 0.5f,
				// V2
				0.5f, -0.5f, 0.5f,
				// V3
				0.5f, 0.5f, 0.5f,
				// V4
				-0.5f, 0.5f, -0.5f,
				// V5
				0.5f, 0.5f, -0.5f,
				// V6
				-0.5f, -0.5f, -0.5f,
				// V7
				0.5f, -0.5f, -0.5f,
				
				// For text coords in top face
				// V8: V4 repeated
				-0.5f, 0.5f, -0.5f,
				// V9: V5 repeated
				0.5f, 0.5f, -0.5f,
				// V10: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V11: V3 repeated
				0.5f, 0.5f, 0.5f,
				
				// For text coords in right face
				// V12: V3 repeated
				0.5f, 0.5f, 0.5f,
				// V13: V2 repeated
				0.5f, -0.5f, 0.5f,
				
				// For text coords in left face
				// V14: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V15: V1 repeated
				-0.5f, -0.5f, 0.5f,
				
				// For text coords in bottom face
				// V16: V6 repeated
				-0.5f, -0.5f, -0.5f,
				// V17: V7 repeated
				0.5f, -0.5f, -0.5f,
				// V18: V1 repeated
				-0.5f, -0.5f, 0.5f,
				// V19: V2 repeated
				0.5f, -0.5f, 0.5f,
			};
			
			float[] texCoords = new float[] {
				0.0f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.5f, 0.0f,
				
				0.0f, 0.0f,
				0.5f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				
				// For text coords in top face
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.0f, 1.0f,
				0.5f, 1.0f,
				
				// For text coords in right face
				0.0f, 0.0f,
				0.0f, 0.5f,
				
				// For text coords in left face
				0.5f, 0.0f,
				0.5f, 0.5f,
				
				// For text coords in bottom face
				0.5f, 0.0f,
				1.0f, 0.0f,
				0.5f, 0.5f,
				1.0f, 0.5f,
			};
			
			int[] indices = new int[] {
				// Front face
				0, 1, 3, 3, 1, 2,
				// Top Face
				8, 10, 11, 9, 8, 11,
				// Right face
				12, 13, 7, 5, 12, 7,
				// Left face
				14, 15, 6, 4, 14, 6,
				// Bottom face
				16, 18, 19, 17, 16, 19,
				// Back face
				4, 6, 7, 5, 4, 7,
			};
			
			Texture texture;
			
			try {
				texture = Texture.create(Monospace.engineResources().get(Resource.Type.TEXTURES, "cube"));
			} catch(Texture.LoadingException e) {
				throw new RuntimeException(e);
			}
			
			List<Mesh> meshes = new ArrayList<>();
			meshes.add(new Mesh(positions, texCoords, indices));
			
			List<Material> materials = new ArrayList<>();
			materials.add(new Material(texture, meshes));
			
			var model = new Model("cube", materials);
			MODELS.put(model.getId(), model);
		}
	}
}
