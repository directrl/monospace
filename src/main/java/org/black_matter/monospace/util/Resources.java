package org.black_matter.monospace.util;

import org.black_matter.monospace.core.Monospace;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Resources {
	
	public final String ASSETS_ROOT;
	
	public Resources(String namespace) {
		ASSETS_ROOT = "/assets/" + namespace + "/";
	}
	
	public String getPath(Type assetType, String resourceName) {
		return ASSETS_ROOT + assetType.path + "/" + resourceName + assetType.extension;
	}
	
	public InputStream getStream(Type assetType, String resourceName) {
		var stream = this.getClass().getResourceAsStream(getPath(assetType, resourceName));
		
		if(stream == null) {
			Monospace.LOGGER.fatal("Could not get resource stream for " + getPath(assetType, resourceName));
		}
		
		return stream;
	}
	
	public URL getUrl(Type assetType, String resourceName) {
		return this.getClass().getResource(getPath(assetType, resourceName));
	}
	
	public byte[] read(Type assetType, String resourceName) {
		try {
			return getStream(assetType, resourceName).readAllBytes();
		} catch(IOException e) {
			Monospace.LOGGER.fatal("Could not read resource " + getPath(assetType, resourceName), e);
			return null;
		}
	}
	
	public String readString(Type assetType, String resourceName) {
		var data = read(assetType, resourceName);
		if(data != null) return new String(data);
		return null;
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
