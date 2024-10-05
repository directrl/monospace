package org.black_matter.monospace.object.prefab;

import lombok.Getter;
import org.black_matter.monospace.core.Monospace;
import org.black_matter.monospace.model.Model;
import org.black_matter.monospace.model.ModelLoader;
import org.black_matter.monospace.object.GameObject;
import org.black_matter.monospace.util.Resource;
import org.black_matter.monospace.util.Resources;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

public class Prefab {

	@Getter private final String id;
	@Getter private final Model model;
	
	private Class<? extends GameObject> objectClass;
	
	private Prefab(String id, Model model) {
		this.id = id;
		this.model = model;
	}
	
	public static Prefab from(String targetNamespace, String prefabName) {
		var res = new Resources(targetNamespace);
		var json = new JSONObject(res.get(Resource.Type.PREFABS, prefabName).readAsString());
		
		var id = json.getString("id");
		
		var modelPath = json.getString("model");
		var model = ModelLoader.load(res.get(Resource.Type.MODELS, modelPath));
		
		var prefab = new Prefab(id, model);
		
		var objectClassName = json.getString("class");
		
		try {
			prefab.objectClass = (Class<? extends GameObject>) Class.forName(objectClassName);
		} catch(ClassNotFoundException e) {
			Monospace.LOGGER.warn(String.format("Unable to get object class for prefab [%s]. We won't be able to instantiate it",
				id));
		}
		
		return prefab;
	}
	
	public static Prefab fromExternal(String assetsDirectory, String prefabName) {
		var json = new JSONObject(Resources.getExternal(assetsDirectory,
			Resource.Type.PREFABS, prefabName).readAsString());
		
		var id = json.getString("id");
		
		var modelPath = json.getString("model");
		var model = ModelLoader.load(Resources.getExternal(assetsDirectory,
			Resource.Type.MODELS, modelPath));
		
		var prefab = new Prefab(id, model);
		
		var objectClassName = json.getString("class");
		
		try {
			prefab.objectClass = (Class<? extends GameObject>) Class.forName(objectClassName);
		} catch(ClassNotFoundException e) {
			Monospace.LOGGER.warn(String.format("Unable to get object class for prefab [%s]. We won't be able to instantiate it",
				id));
		}
		
		return prefab;
	}
	
	public GameObject instantiate() {
		if(objectClass == null) return null;
		
		Constructor<?> ctor = null;
		
		try {
			ctor = objectClass.getDeclaredConstructor(Prefab.class);
		} catch(Exception e) { }
		
		try {
			if(ctor == null) {
				try {
					ctor = objectClass.getDeclaredConstructor();
				} catch(NoSuchMethodException e) {
					Monospace.LOGGER.warn(String.format("Unable to instantiate prefab [%s]", id), e);
				}
				
				return (GameObject) ctor.newInstance();
			} else {
				return (GameObject) ctor.newInstance(this);
			}
		} catch(Exception e) {
			Monospace.LOGGER.warn(String.format("Unable to instantiate prefab [%s]", id), e);
		}
		
		return null;
	}
}
