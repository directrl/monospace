package org.black_matter.monospace.tools.worldeditor.objects;

import lombok.Getter;
import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.object.components.Renderable;
import org.black_matter.monospace.object.objects.ModelObject;
import org.black_matter.monospace.render.Material;
import org.black_matter.monospace.render.Mesh;
import org.black_matter.monospace.render.Texture;
import org.black_matter.monospace.tools.worldeditor.WorldEditor;
import org.black_matter.monospace.util.Resource;
import org.joml.Vector3f;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class CoordinateGrid extends GameObject implements Renderable {
	
	private static final HashMap<Class<? extends Component>, Component> COMPONENTS = new HashMap<>() {{
		put(Renderable.class, (Renderable<CoordinateGrid>) o -> o.render(o));
	}};
	
	private final Model model;
	
	@Getter private final float size;
	
	public CoordinateGrid(int lines, float spacing) {
		super(COMPONENTS);
		
		size = lines * spacing;
		
		float[] vertices = new float[lines * 2 * 2 * 3];
		float[] texCoords = new float[vertices.length / 3 * 2];
		int[] indices = new int[vertices.length / 3];
		
		int vi = -1;
		int ti = -1;
		int ii = -1;
		
		for(int i = 0; i < lines; i++) {
			var startX = new Vector3f(
				0,
				0,
				i * spacing
			);
			
			var endX = new Vector3f(
				lines * spacing,
				0,
				i * spacing
			);
			
			var startY = new Vector3f(
				i * spacing,
				0,
				0
			);
			
			var endY = new Vector3f(
				i * spacing,
				0,
				lines * spacing
			);
			
			// X coordinate line
			vertices[++vi] = startX.x;
			vertices[++vi] = startX.y;
			vertices[++vi] = startX.z;
			texCoords[++ti] = 0;
			texCoords[++ti] = 0;
			indices[++ii] = ii;
			
			vertices[++vi] = endX.x;
			vertices[++vi] = endX.y;
			vertices[++vi] = endX.z;
			texCoords[++ti] = 0;
			texCoords[++ti] = 0;
			indices[++ii] = ii;
			
			// Y coordinate line
			vertices[++vi] = startY.x;
			vertices[++vi] = startY.y;
			vertices[++vi] = startY.z;
			texCoords[++ti] = 0.5f;
			texCoords[++ti] = 0;
			indices[++ii] = ii;
			
			vertices[++vi] = endY.x;
			vertices[++vi] = endY.y;
			vertices[++vi] = endY.z;
			texCoords[++ti] = 0.5f;
			texCoords[++ti] = 0;
			indices[++ii] = ii;
		}
		
		var mesh = new LineMesh(vertices, texCoords, indices);
		var material = new Material(Texture.create(
			WorldEditor.gameResources().get(Resource.Type.TEXTURES, "coordinate_grid")),
			Collections.singletonList(mesh));
		model = new Model("coordinateGrid", Collections.singletonList(material), Collections.emptyList());
	}
	
	@Override
	public void render(GameObject o) {
		model.render();
	}
	
	// TODO support for other element types should be implemented in standard Mesh
	private static class LineMesh extends Mesh {
		
		public LineMesh(float[] positions, float[] texCoords, int[] indices) {
			super(positions, texCoords, indices);
		}
		
		@Override
		public void render() {
			glBindVertexArray(getVaoId());
			glDrawElements(GL_LINES, getVertexCount(), GL_UNSIGNED_INT, 0);
		}
	}
}
