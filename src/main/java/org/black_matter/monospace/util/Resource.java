package org.black_matter.monospace.util;

import lombok.Getter;
import org.black_matter.monospace.core.Monospace;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Resource {
	
	@Getter public final Type type;
	@Getter public final String name;
	@Getter public final String path;
	
	public Resource(Type type, String name, String path) {
		this.type = type;
		this.name = name;
		this.path = path;
	}
	
	public byte[] readAllBytes() {
		try(var stream = asStream()) {
			return stream.readAllBytes();
		} catch(IOException e) {
			Monospace.LOGGER.fatal("Could not read " + this, e);
			return null;
		}
	}
	
	public String readAsString() {
		var data = readAllBytes();
		if(data != null) return new String(data);
		return null;
	}
	
	public InputStream asStream() {
		var stream = this.getClass().getResourceAsStream(path);
		
		if(stream == null) {
			Monospace.LOGGER.fatal("Could not get resource stream for " + this);
		}
		
		return stream;
	}
	
	public URL asUrl() {
		return this.getClass().getResource(path);
	}
	
	@Override
	public String toString() {
		return String.format("Resource[type=%s, path=%s]", type.name(), path);
	}
	
	public enum Type {
		
		SHADERS("shaders", ""),
		TEXTURES("textures", ".png"),
		MODELS("models", ".gltf"),
		LOCALISATION("i18n", ".properties"),
		SOUND_EFFECTS("audio/sfx", ".wav"),
		MUSIC("audio/music", ".ogg"),
		CUSTOM("", "");
		
		public final String path;
		public final String extension;
		
		Type(String path, String extension) {
			this.path = path;
			this.extension = extension;
		}
	}
}
