package org.black_matter.monospace.render.gl;

import lombok.Getter;
import org.joml.Matrix4f;
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
			
			if(location == null) {
				location = create(name);
			}
			
			glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
		}
	}
}
