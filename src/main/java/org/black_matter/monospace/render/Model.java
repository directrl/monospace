package org.black_matter.monospace.render;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model implements Closeable {
	
	@Getter private final String id;
	@Getter private List<Mesh> meshes;
	
	public Model(String id, List<Mesh> meshes) {
		this.id = id;
		this.meshes = meshes;
	}
	
	public void render() {
		meshes.forEach(Mesh::render);
	}
	
	@SneakyThrows
	@Override
	public void close() {
		meshes.forEach(Mesh::close);
	}
	
	public static class Registry {
		
		public static final Map<String, Model> MODELS = new HashMap<>();
		
		static {
			float[] positions = new float[]{
				// VO
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
			};
			float[] colors = new float[]{
				0.5f, 0.0f, 0.0f,
				0.0f, 0.5f, 0.0f,
				0.0f, 0.0f, 0.5f,
				0.0f, 0.5f, 0.5f,
				0.5f, 0.0f, 0.0f,
				0.0f, 0.5f, 0.0f,
				0.0f, 0.0f, 0.5f,
				0.0f, 0.5f, 0.5f,
			};
			int[] indices = new int[]{
				// Front face
				0, 1, 3, 3, 1, 2,
				// Top Face
				4, 0, 3, 5, 4, 3,
				// Right face
				3, 2, 7, 5, 3, 7,
				// Left face
				6, 1, 0, 6, 0, 4,
				// Bottom face
				2, 1, 6, 2, 6, 7,
				// Back face
				7, 6, 4, 7, 4, 5,
			};
			
			MODELS.put("quad", new Model("quad", new ArrayList<>() {{
				add(new Mesh(positions, colors, indices));
			}}));
		}
	}
}
