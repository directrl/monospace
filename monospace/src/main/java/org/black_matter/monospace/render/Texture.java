package org.black_matter.monospace.render;

import lombok.Getter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.util.Resource;
import org.lwjgl.assimp.AITexture;
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
	
	public static final Texture DEFAULT_TEXTURE;
	
	static {
		DEFAULT_TEXTURE = Texture.create(new Resource(
			Resource.Type.TEXTURES,
			"missingno",
			"/assets/monospace/textures/default.png"
		));
	}
	
	@Getter private final Resource resource;
	
	@Getter private final int id;
	@Getter private final int width;
	@Getter private final int height;
	
	Texture(Resource resource, int id, int width, int height) {
		this.resource = resource;
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public static Texture create(Resource resource, AITexture aiTexture) {
		var cachedTexture = Cache.TEXTURES.get(resource.name);
		if(cachedTexture != null) return cachedTexture;
		
		return loadFromBuffer(resource, aiTexture.pcDataCompressed());
	}
	
	public static Texture create(Resource resource) {
		var cachedTexture = Cache.TEXTURES.get(resource.name);
		if(cachedTexture != null) return cachedTexture;
		
		byte[] texData;
		
		try {
			texData = resource.readAllBytes();
		} catch(Exception e) {
			Monospace.LOGGER.error("Could not load texture",
				new LoadingException(resource.getPath()));
			return DEFAULT_TEXTURE;
		}
		
		var texBuffer = ByteBuffer.allocateDirect(texData.length).order(ByteOrder.nativeOrder());
		texBuffer.put(texData);
		texBuffer.position(0);
		
		return loadFromBuffer(resource, texBuffer);
	}
	
	private static Texture loadFromBuffer(Resource resource, ByteBuffer buffer) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer wBuffer = stack.mallocInt(1);
			IntBuffer hBuffer = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			var image = stbi_load_from_memory(buffer, wBuffer, hBuffer, channels, 4);
			
			if(image == null) {
				Monospace.LOGGER.error("Could not load texture",
					new LoadingException(resource.getPath()));
				return DEFAULT_TEXTURE;
			}
			
			int width = wBuffer.get();
			int height = hBuffer.get();
			
			int id = glGenTextures();
			
			var texture = new Texture(resource, id, width, height);
			texture.bind();
			
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, Monospace.engineSettings().graphics().getTextureFilter());
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, Monospace.engineSettings().graphics().getTextureFilter());
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			
			if(Monospace.engineSettings().graphics().isTextureMipmapping()) {
				glGenerateMipmap(GL_TEXTURE_2D);
			}
			
			Monospace.LOGGER.trace("Caching texture " + texture);
			Cache.TEXTURES.put(resource.name, texture);
			return texture;
		}
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	@Override
	public void close() {
		glDeleteTextures(id);
		Cache.TEXTURES.remove(resource.name);
	}
	
	@Override
	public String toString() {
		return String.format("Texture{name=%s, width=%d, height=%d, resource=%s}",
			resource.name, width, height, resource);
	}
	
	private static class Cache {
		
		private static final Map<String, Texture> TEXTURES = new HashMap<>();
		
		public static void close() {
			TEXTURES.values().forEach(Texture::close);
		}
	}
	
	public static class LoadingException extends Exception {
		
		public LoadingException(String texturePath) {
			super("Error occurred while loading texture " + texturePath + ": " + stbi_failure_reason());
		}
	}
}
