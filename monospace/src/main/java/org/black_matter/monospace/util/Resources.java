package org.black_matter.monospace.util;

import java.nio.file.Path;

public class Resources {
	
	public final String ASSETS_ROOT;
	
	public Resources(String namespace) {
		ASSETS_ROOT = "/assets/" + namespace + "/";
	}
	
	public Resource get(Resource.Type type, String resourceName) {
		return new Resource(
			type,
			resourceName,
			ASSETS_ROOT + type.path + "/" + resourceName + type.extension
		);
	}
	
	public static Resource getExternal(String namespaceDirectory, Resource.Type type, String resourceName) {
		return new Resource(
			Resource.Type.EXTERNAL,
			resourceName,
			Path.of(namespaceDirectory, type.path, resourceName + type.extension).toString()
		);
	}
}
