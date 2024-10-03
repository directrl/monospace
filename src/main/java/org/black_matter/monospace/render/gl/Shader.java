package org.black_matter.monospace.render.gl;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL30.*;

public record Shader(int type, String code) {
	
	public int compile() throws CompilationException {
		int id = glCreateShader(type);
		
		if(id == 0) {
			throw new RuntimeException("Could not create a GL shader");
		}
		
		glShaderSource(id, code);
		glCompileShader(id);
		
		if(glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
			throw new CompilationException(id);
		}
		
		return id;
	}
	
	public static Shader fromResources(int id, String path) throws IOException {
		String code = new String(Shader.class.getResourceAsStream("/shaders/" + path).readAllBytes());
		return new Shader(id, code);
	}
	
	public static class CompilationException extends Exception {
		
		public CompilationException(int id) {
			super("Error occurred during shader compilation: " + glGetShaderInfoLog(id));
		}
	}
}
