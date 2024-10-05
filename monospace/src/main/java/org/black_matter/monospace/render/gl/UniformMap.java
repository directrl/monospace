package org.black_matter.monospace.render.gl;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class UniformMap {
	
	@Getter private int programId;
	private Map<String, Integer> uniforms = new HashMap<>();
	
	public UniformMap(int programId) {
		this.programId = programId;
	}
	
	public int create(String name) {
		int location = glGetUniformLocation(programId, name);
		
		if(location < 0) {
			throw new RuntimeException(String.format("Could not find the location for [%s] in program [%d]",
				name, programId));
		}
		
		uniforms.put(name, location);
		return location;
	}
	
	public void load(String name, Matrix4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			Integer location = uniforms.get(name);
			if(location == null) location = create(name);
			
			glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
		}
	}
	
	public void load(String name, Vector4f value) {
		Integer location = uniforms.get(name);
		if(location == null) location = create(name);
		
		glUniform4f(location, value.x, value.y, value.z, value.w);
	}
	
	public void load(String name, int value) {
		Integer location = uniforms.get(name);
		if(location == null) location = create(name);
		
		glUniform1i(location, value);
	}
}
