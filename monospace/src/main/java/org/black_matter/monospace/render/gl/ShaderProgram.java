package org.black_matter.monospace.render.gl;

import lombok.Getter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.util.Resource;
import org.black_matter.monospace.util.Resources;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {
	
	@Getter private final Shader[] program;
	@Getter private final UniformMap uniforms;
	
	@Getter private int id;
	
	public ShaderProgram(Shader[] program) {
		this.id = glCreateProgram();
		
		if(this.id == 0) {
			throw new RuntimeException("Could not create a shader program");
		}
		
		this.program = program;
		this.uniforms = new UniformMap(this.id);
	}
	
	public void build() throws Shader.CompilationException, LinkingException {
		List<Integer> shaders = new ArrayList<>();
		
		for(var shader : program) {
			int shaderId;
			
			shaderId = shader.compile();
			
			glAttachShader(id, shaderId);
			shaders.add(shaderId);
		}
		
		glLinkProgram(id);
		
		if(glGetProgrami(id, GL_LINK_STATUS) == 0) {
			throw new LinkingException(id);
		}
		
		// clean-up
		for(var shaderId : shaders) {
			glDetachShader(id, shaderId);
			glDeleteShader(shaderId);
		}
	}
	
	public void bind() {
		glUseProgram(id);
	}
	
	public boolean validate() throws ValidationException {
		glValidateProgram(id);
		
		if(glGetProgrami(id, GL_VALIDATE_STATUS) == 0) {
			throw new ValidationException(id);
		}
		
		return true;
	}
	
	public static class LinkingException extends Exception {
		
		public LinkingException(int id) {
			super("Error occurred during program linking: " + glGetProgramInfoLog(id));
		}
	}
	
	public static class ValidationException extends Exception {
		
		public ValidationException(int id) {
			super("Error occurred during program validation: " + glGetProgramInfoLog(id));
		}
	}
	
	public static class Registry {
	
		private static final Map<String, ShaderProgram> programs = new HashMap<>();
		
		static {
			try {
				programs.put("world", new ShaderProgram(new Shader[] {
					new Shader(GL_VERTEX_SHADER, Monospace.engineResources()
						.get(Resource.Type.SHADERS, "world.vert")
						.readAsString()),
					new Shader(GL_FRAGMENT_SHADER, Monospace.engineResources()
						.get(Resource.Type.SHADERS, "world.frag")
						.readAsString())
				}));
			} catch(RuntimeException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		public static Map<String, ShaderProgram> get() { return Collections.unmodifiableMap(programs); }
		public static ShaderProgram get(String name) { return programs.get(name); }
	}
}
