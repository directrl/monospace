package org.black_matter.monospace.render;

import lombok.Getter;
import org.black_matter.monospace.util.Resource;
import org.lwjgl.system.MemoryStack;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture implements Closeable {
	
	public static final Resource DEFAULT_TEXTURE = new Resource(
		Resource.Type.TEXTURES,
		"missingno",
		"/assets/monospace/textures/default.png"
	);
	
	@Getter private final Resource resource;
	
	@Getter private final int id;
	@Getter private final int width;
	@Getter private final int height;
	
	private Texture(Resource resource, int id, int width, int height) {
		this.resource = resource;
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public static Texture create(Resource resource) throws LoadingException {
		var cachedTexture = Cache.get(resource);
		if(cachedTexture != null) return cachedTexture;
		
		var texData = resource.readAllBytes();
		ByteBuffer texBuffer = ByteBuffer.allocateDirect(texData.length).order(ByteOrder.nativeOrder());
		texBuffer.put(texData);
		texBuffer.position(0);
		
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer wBuffer = stack.mallocInt(1);
			IntBuffer hBuffer = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			var image = stbi_load_from_memory(texBuffer, wBuffer, hBuffer, channels, 4);
			
			if(image == null) {
				throw new LoadingException(resource.getPath());
			}
			
			int width = wBuffer.get();
			int height = hBuffer.get();
			
			int id = glGenTextures();
			
			var texture = new Texture(resource, id, width, height);
			texture.bind();
			
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			glGenerateMipmap(GL_TEXTURE_2D);
			
			return texture;
		}
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	@Override
	public void close() {
		glDeleteTextures(id);
	}
	
	public static class Cache {
		
		private static final Map<String, Texture> textures = new HashMap<>();
		
		static {
			try {
				Texture.create(DEFAULT_TEXTURE);
			} catch(LoadingException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		public static Texture get(Resource resource) {
			if(textures.containsKey(resource.name)) {
				return textures.get(resource.name);
			}
			
			return textures.get("missingno");
		}
		
		public static void set(Resource resource, Texture texture) {
			if(resource.name.equals(DEFAULT_TEXTURE.name)) {
				throw new InvalidParameterException("Cannot change the default texture");
			}
			
			textures.put(resource.name, texture);
		}
		
		public static void remove(Resource resource) {
			textures.remove(resource.name);
		}
		
		public static void close() {
			textures.values().forEach(Texture::close);
		}
	}
	
	public static class LoadingException extends Exception {
		
		public LoadingException(String texturePath) {
			super("Error occurred while loading texture " + texturePath + ": " + stbi_failure_reason());
		}
	}
}
