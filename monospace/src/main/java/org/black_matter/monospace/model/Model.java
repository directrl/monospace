package org.black_matter.monospace.model;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.render.Material;
import org.black_matter.monospace.render.Mesh;
import org.black_matter.monospace.render.Texture;
import org.black_matter.monospace.render.gl.ShaderProgram;
import org.black_matter.monospace.util.Resource;
import org.black_matter.monospace.world.GameWorld;
import org.lwjgl.opengl.GL30;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model implements Closeable {
	
	@Getter private final String id;
	@Getter @Setter private List<Material> materials;
	
	public Model(String id, List<Material> materials) {
		this.id = id;
		this.materials = materials;
	}
	
	public void render() {
		materials.forEach(material -> {
			GL30.glActiveTexture(GL30.GL_TEXTURE0);
			material.getTexture().bind();
			GameWorld.WORLD_SHADER.getUniforms().load("material.diffuseColor", material.getDiffuseColor());
			material.getMeshes().forEach(Mesh::render);
		});
	}
	
	@Override
	public String toString() {
		return String.format("Model{id=%s, materials=%s}", id, materials.toString());
	}
	
	@SneakyThrows
	@Override
	public void close() {
		materials.forEach(Material::close);
	}
}
