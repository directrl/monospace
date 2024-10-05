package org.black_matter.monospace.render;

import lombok.Getter;
import lombok.Setter;
import org.black_matter.monospace.util.Resource;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Material implements Closeable {
	
	@Getter @Setter private Texture texture;
	@Getter private final List<Mesh> meshes;
	
	public Material() {
		this.meshes = new ArrayList<>();
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
