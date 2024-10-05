package org.black_matter.monospace.render;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.util.Resource;
import org.joml.Vector4f;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Material implements Closeable {
	
	@Getter private final List<Mesh> meshes;
	@Getter @Setter private Texture texture;
	@Getter @Setter private Vector4f diffuseColor;
	
	public Material() {
		this.meshes = new ArrayList<>();
		this.diffuseColor = new Vector4f(1, 1, 1, 1);
	}
	
	public Material(Texture texture, List<Mesh> meshes) {
		this.texture = texture;
		this.meshes = meshes;
	}
	
	@Override
	public void close() {
		meshes.forEach(Mesh::close);
	}
}
